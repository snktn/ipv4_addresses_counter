package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

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

    public static int countUniqueAddresses(File file) throws FileNotFoundException, InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        AddressCounter counter = new AddressCounter();
        executorService.submit(counter);
        Reader reader = new Reader(file);
        for (int i = 0; i < nThreads - 1; i++) {
            executorService.submit(new Parser(counter, reader));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()){
            LockSupport.parkNanos(1000000);
        }
        return counter.getUniqueAddressesCount();
    }
}