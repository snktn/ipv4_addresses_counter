package ru.snktn.ipv4AddressesCounter;

import java.util.BitSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class AddressCounter extends Thread implements Runnable{
    private final AtomicInteger counter = new AtomicInteger(0);
    private static final AtomicBoolean shutDown = new AtomicBoolean(false);
    public static void shutdown () {
        shutDown.set(true);
    }
    private final BitSet [] bitSets = new BitSet[2];
    public AddressCounter (){
        for (int i = 0; i < 2; i++) {
            bitSets[i] = new BitSet(Integer.MAX_VALUE);
        }
    }

    public int getUniqueAddressesCount() {
        return counter.get();
    }
    protected final BlockingQueue<int []> queue = new ArrayBlockingQueue<>(16);

    @Override
    public void run() {
        waitTask();
        take();
        counter.addAndGet(bitSets[0].cardinality() + bitSets[1].cardinality());
    }
    public void add (int [] decimalAddresses) throws InterruptedException {
        queue.put(decimalAddresses);
    }

    private void setBits (int [] decimalAddresses) throws IndexOutOfBoundsException {
        for (int address : decimalAddresses) {
            if (address < 0) bitSets[0].set(Math.abs(address));
            else if (address > 0) bitSets[1].set(address);
        }
    }

    private void waitTask() {
        while (true) {
            if (queue.isEmpty()) {
                LockSupport.parkNanos(1000);
            } else break;
        }
    }

    private void take() {
        while (!shutDown.get() || !queue.isEmpty()) {
            try {
                setBits(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }
}