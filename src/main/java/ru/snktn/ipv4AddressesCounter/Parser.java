package ru.snktn.ipv4AddressesCounter;

import java.util.ArrayList;

public class Parser extends Thread implements Runnable, IPv4AddressExtractor {
    public static final int CHUNK_SIZE = 491500;
    private final AddressCounter [][] addressCounters;
    private final Reader reader;
    private final ArrayList<Byte> list = new ArrayList<>(15);
    private final byte [] bb = new byte[CHUNK_SIZE];
    public Parser (AddressCounter[][] addressCounters, Reader reader) {
        this.addressCounters = addressCounters;
        this.reader = reader;
    }

    @Override
    public void run() {
        while (reader.hasNext()) {
            try {
                int nCounters = addressCounters[0].length;

                // get list of decimal values of ipv4 addresses from the received chunk

                //ArrayList<Integer> decimalAddressList = extract(reader.read());

                // initializes lists of indexes for bitmaps where:
                // index is (decimal value of ipv4 address / number of counters)
                // i - is 0 for negative and 1 for positive values
                // j - is abs(decimal value of ipv4 address % number of counters)
                ArrayList<Integer>[][] indexLists = new ArrayList[2][nCounters];

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < nCounters; j++) {
                        indexLists[i][j] = new ArrayList<>(Reader.CHUNK_SIZE / 15);
                    }
                }

                // get array of decimal values of ipv4 addresses from the received chunk
                list.clear();
                distribute(extract(concatAndReverse(reader.read(bb, list))), indexLists, nCounters);
                submit(addressCounters, indexLists);
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        AddressCounter.shutdown();
    }

    // distributes indexes to corresponding lists
    private void distribute (int [] ints, ArrayList<Integer>[][] indexLists, int nCounters) {
        if (ints.length > 0) {
            for (int i = 1; i < ints[0]; i++) {
                int n;
                int m;
                if (ints[i] < 0) n = 0;
                else n = 1;
                m = Math.abs(ints[i] % nCounters);
                indexLists[n][m].add(ints[i] / nCounters);
            }
        }
    }

    // submit indexes list to corresponding counters
    private void submit (AddressCounter[][] addressCounters, ArrayList<Integer>[][] indexLists) {
        for (int i = 0; i < indexLists.length; i++) {
            for (int j = 0; j < indexLists[i].length; j++) {
                addressCounters[i][j].add(indexLists[i][j]);
            }
        }
    }

    private byte [] concatAndReverse (byte[][] arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte [] bytes = new byte[length];
        int i = 0;
        for (int j = arrays.length - 1; j >= 0; j--) {
            for (int k = arrays[j].length - 1; k >= 0; k--) {
                bytes[i] = arrays[j][k];
                i++;
            }
        }
        return bytes;
    }
}
