package ru.snktn.ipv4AddressesCounter;

import java.util.BitSet;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class AddressCounter extends Thread implements Runnable{
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final AtomicBoolean shutDown = new AtomicBoolean(false);
    public static void shutdown () {
        shutDown.set(true);
    }
    private final BitSet [] bitSets;
    public AddressCounter (){
        this.bitSets = new BitSet[]{new BitSet(Integer.MAX_VALUE), new BitSet(Integer.MAX_VALUE)};
    }

    synchronized public int getUniqueAddressesCount() {
        return counter.get();
    }

    protected final LinkedTransferQueue<int []> queue = new LinkedTransferQueue<>();

    @Override
    public void run() {
        waitTask();
        take();
        counter.addAndGet(bitSets[0].cardinality() + bitSets[1].cardinality());
    }
    public void add (int [] decimalAddresses) {
        queue.put(decimalAddresses);
    }

    private void setBits (int [] decimalAddresses) {
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
            }
        }
    }
}