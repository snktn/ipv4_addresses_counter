package ru.snktn.ipv4AddressesCounter;

import java.util.BitSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class AddressCounter extends Thread implements Runnable{
    private final AtomicInteger count = new AtomicInteger(0);
    private volatile static boolean started = false;
    private static volatile boolean shutDown = false;
    synchronized static void shutdown () {
        shutDown = true;
    }
    private final BitSet [] bitSets = new BitSet[2];
    AddressCounter (){
        for (int i = 0; i < 2; i++) {
            bitSets[i] = new BitSet(Integer.MAX_VALUE);
        }
    }

    synchronized int getUniqueAddressesCount() {
        return bitSets[0].cardinality() + bitSets[1].cardinality();
    }
    final BlockingQueue<int []> queue = new ArrayBlockingQueue<>(16);

    @Override
    public void run() {
        try {
            take();
            takeLast();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        count.addAndGet(bitSets[0].cardinality() + bitSets[1].cardinality());
    }

    void add (int [] decimalAddresses) throws InterruptedException {
        queue.put(decimalAddresses);
        if (!started) started = true;
    }

    private void setBits (int [] decimalAddresses) throws IndexOutOfBoundsException {
        for (int address : decimalAddresses) {
            if (address < 0) {
                bitSets[0].set(Math.abs(address));
            }
            else if (address > 0) {
                bitSets[1].set(address);
            }
        }
    }

    private void take() throws InterruptedException{
        while (!started) {
            sleep(1);
        }

        while (!shutDown || !queue.isEmpty()) {
            try {
                setBits(queue.take());
            }
            catch (IndexOutOfBoundsException ignored) {}
        }
    }

    private void takeLast() throws InterruptedException {
        sleep(10);
        while (!queue.isEmpty() || Parser.getActiveParsersCount() != 0) {
            int [] ints = queue.poll();
                if (ints != null) setBits(ints);
        }
    }
}