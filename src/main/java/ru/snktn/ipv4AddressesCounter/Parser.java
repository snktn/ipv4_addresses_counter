package ru.snktn.ipv4AddressesCounter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser extends Thread implements Runnable, IPv4AddressExtractor {
    static int CHUNK_SIZE = 523249;
    private static final AtomicInteger activeParsersCount = new AtomicInteger();
    synchronized static int getActiveParsersCount() {
        return activeParsersCount.get();
    }
    private final AddressCounter addressCounter;
    private final Reader reader;
    private final ArrayList<Byte> list = new ArrayList<>(15);
    private final byte [] bytes;
    Parser (AddressCounter addressCounter, Reader reader) {
        activeParsersCount.getAndIncrement();
        this.addressCounter = addressCounter;
        this.reader = reader;
        bytes = new byte[CHUNK_SIZE];
    }

    @Override
    public void run() {
        while (reader.hasNext()) {
            try {
               addressCounter.add(extract(concatAndReverse(reader.read(bytes, list))));
               list.clear();
            }
            catch (ArrayIndexOutOfBoundsException ignored) {}
            catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        activeParsersCount.decrementAndGet();
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
