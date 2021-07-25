package ru.snktn.ipv4AddressesCounter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class AddressCounter extends Thread implements Runnable{
    private static final AtomicInteger counter = new AtomicInteger(0);
    public static int getUniqueAddressesCount() {
        return counter.get();
    }

    private static final AtomicBoolean shutDown = new AtomicBoolean(false);
    public static void shutdown () {
        shutDown.set(true);
    }

    private static final AtomicBoolean lastChunk = new AtomicBoolean(false);
    public static void lastChunk () {
        lastChunk.set(true);
    }

    private final BitSet bitSet;
    public AddressCounter (int nCounters){
        this.bitSet = new BitSet(Integer.MAX_VALUE / nCounters );
    }
    protected final LinkedTransferQueue<ArrayList<Integer>> queue = new LinkedTransferQueue<>();

    @Override
    public void run() {
        waitTask();
        take();
        pollLastChunk();
        int count = bitSet.cardinality();
        counter.addAndGet(count);
    }

    public void add (ArrayList<Integer> decimalAddress) {
        queue.put(decimalAddress);
    }

    private void setBits (ArrayList<Integer> decimalAddressList) {
        try {
            for (int index : decimalAddressList) {
                bitSet.set(Math.abs(index));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
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

    private void pollLastChunk() {
        while (!lastChunk.get() || !queue.isEmpty()) {
            ArrayList<Integer> indexList = queue.poll();
            if (indexList != null) setBits(indexList);
            else {
                LockSupport.parkNanos(1000);
            }
        }
    }
}