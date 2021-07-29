package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

public class ChunkFabric extends Thread implements Runnable {
    private static final int CHUNK_SIZE = 491500;
    private final AddressCounter[][] addressCounters;
    private final FileInputStream reader;
    private final RandomAccessFile raf;
    private int nThreads = 1;
    private long offSet = 0L;

    public ChunkFabric (File file, AddressCounter[][] addressCounters) throws FileNotFoundException {
        this.reader = new FileInputStream(file);
        this.raf = new RandomAccessFile(file, "r");
        this.addressCounters = addressCounters;
    }

    @Override
    public void run() {

        try {
            markFile();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //find last digit of address after skipped bytes and submit bytes to the parser
    private void markFile () throws IOException, InterruptedException {
        if (Runtime.getRuntime().availableProcessors() > 1) nThreads = Runtime.getRuntime().availableProcessors() - 1;
        final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(nThreads * 16);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                nThreads, nThreads, 0, TimeUnit.MILLISECONDS, queue
        );
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        int c = 0;
        while (c > -1) {

            raf.seek(offSet);
            raf.skipBytes(CHUNK_SIZE);

            c =raf.read();
            if (c == -1) {
                break;
            }
            if (c == 46 || c >= 48 && c <= 57) {
                while (c == 46 || c >= 48 && c <= 57 ){
                    c = raf.read();
                }
            }
            else {
                while ((c < 48 || c > 57) && c != 1 ) c = raf.read();
            }

            byte[] bytes = new byte[(int) (raf.getFilePointer() - 2 - offSet + 1)];
            reader.read(bytes);
            executor.submit(new Parser(bytes, addressCounters));
            offSet = raf.getFilePointer() - 1;
        }

        executor.shutdown();
        AddressCounter.shutdown();
        while (!executor.isTerminated()) {
            LockSupport.parkNanos(1000);
        }
        byte[] bytes = new byte[(int) (raf.length() - 1  - offSet + 1)];
        reader.read(bytes);

        raf.close();
        reader.close();
        Parser lastChunkParser = new Parser(bytes, addressCounters);
        lastChunkParser.start();
        lastChunkParser.join();
        AddressCounter.lastChunk();
    }
}