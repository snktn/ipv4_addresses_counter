package ru.snktn.ipv4AddressesCounter;

import java.util.ArrayList;

public class Parser extends Thread implements Runnable, IPv4AddressExtractor {
    private final AddressCounter [][] addressCounters;
    private final byte [] bytes;
    public Parser (byte [] bytes, AddressCounter[][] addressCounters ) {
        this.addressCounters = addressCounters;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        try {
            int nCounters = addressCounters[0].length;

            // get list of decimal values of ipv4 addresses from the received chunk
            ArrayList<Integer> decimalAddressList = extract(bytes);

            // initializes lists of indexes for bitmaps where:
            // index is (decimal value of ipv4 address / number of counters)
            // i - is 0 for negative and 1 for positive values
            // j - is abs(decimal value of ipv4 address % number of counters)
            ArrayList<Integer>[][] indexLists = new ArrayList[2][nCounters];

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < nCounters; j++) {
                    indexLists[i][j] = new ArrayList<>(bytes.length / 15);
                }
            }

            distribute(decimalAddressList, indexLists, nCounters);
            submit(addressCounters, indexLists);

        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
