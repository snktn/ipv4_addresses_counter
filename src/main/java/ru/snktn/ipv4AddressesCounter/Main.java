package ru.snktn.ipv4AddressesCounter;

import java.io.*;

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

    private static int countUniqueAddresses(File file) throws FileNotFoundException, InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors();
        int nCounters;
        if (nThreads % 2 == 0) nCounters = nThreads;
        else nCounters = nThreads + 1;
        AddressCounter[][] addressCounters = new AddressCounter[2][nCounters];
        for (int i = 0; i < addressCounters.length; i++) {
            for (int j = 0; j < addressCounters[i].length; j++) {
                addressCounters[i][j] = new AddressCounter(nCounters);
            }
        }
        ChunkFabric chunkFabric = new ChunkFabric(file, addressCounters);
        chunkFabric.start();
        for (AddressCounter[] addressCounter : addressCounters) {
            for (AddressCounter counter : addressCounter) {
                counter.start();
            }
        }
        chunkFabric.join();
        for (AddressCounter[] addressCounter : addressCounters) {
            for (AddressCounter counter : addressCounter) {
                counter.join();
            }
        }
        return AddressCounter.getUniqueAddressesCount();
    }
}