package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.ArrayList;

public class Reader {
    public static final int CHUNK_SIZE = 491500;
    private final FileInputStream reader;
    private byte [] bytes = new byte[CHUNK_SIZE];
    private boolean hasNext = true;
    public boolean hasNext() {
        return hasNext;
    }

    public Reader(File file) throws FileNotFoundException {
        this.reader = new FileInputStream(file);
    }

    synchronized public byte[] read () throws IOException, ArrayIndexOutOfBoundsException {

        int c;
        final ArrayList<Byte> list = new ArrayList<>(2);

        reader.read(bytes);
        c = bytes[bytes.length - 1];
        if (c == 46 || c >= 48 && c <= 57 )
            while (c == 46 || c >= 48 && c <= 57 ){
                c = reader.read();
                list.add((byte) c );
            }
        if (reader.available() < CHUNK_SIZE) {
            AddressCounter.shutdown();
            bytes = new byte[reader.available()];
            AddressCounter.lastChunk();
            hasNext = false;
        }
            return concatAndReverse(bytes, list);
    }

    private byte [] concatAndReverse (byte [] bytes, ArrayList<Byte> list) {
        byte [] newBytes = new byte[bytes.length + list.size()];
        int i = 0;
        for (int j = list.size() - 1; j >= 0; j--) {
            newBytes[i] = list.get(j);
            i++;
        }
        for (int j = bytes.length - 1; j >= 0; j--) {
            newBytes[i] = bytes[j];
            i++;
        }
        return newBytes;
    }
}
