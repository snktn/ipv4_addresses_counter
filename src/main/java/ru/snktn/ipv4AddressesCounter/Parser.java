package ru.snktn.ipv4AddressesCounter;

import java.util.ArrayList;

public class Parser extends Thread implements Runnable, IPv4AddressExtractor {
    public static final int CHUNK_SIZE = 491500;
    private final AddressCounter addressCounter;
    private final Reader reader;
    private final ArrayList<Byte> list = new ArrayList<>(15);
    private final byte [] bb = new byte[CHUNK_SIZE];
    public Parser (AddressCounter addressCounter, Reader reader) {
        this.addressCounter = addressCounter;
        this.reader = reader;
    }

    @Override
    public void run() {
        while (reader.hasNext()) {
            try {
                list.clear();
                byte [][] ba = reader.read(bb, list);
                addressCounter.add(extract(concatAndReverse(ba)));
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
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
