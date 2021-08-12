package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {
            long startTime = System.currentTimeMillis();
            System.out.println(countUniqueAddresses((new File(args[0]))));
            System.out.println((System.currentTimeMillis() - startTime) + " ms");
        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Specify the filename as a program argument");
            System.exit(0);
        }
    }

    static int countUniqueAddresses(File file) throws FileNotFoundException, InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), Parser.CHUNK_SIZE + 15);
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        AddressCounter counter = new AddressCounter();
        executorService.submit(counter);
        Reader reader = new Reader(bis);
        for (int i = 0; i < nThreads - 1; i++) {
            executorService.submit(new Parser(counter, reader));
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counter.getUniqueAddressesCount();
    }
}