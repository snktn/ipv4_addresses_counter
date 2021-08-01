package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.ArrayList;

public class Reader {
    public static final int CHUNK_SIZE = 491500;
    private final BufferedInputStream reader;
    private boolean hasNext = true;
    public boolean hasNext() {
        return hasNext;
    }

    public Reader(File file) throws FileNotFoundException {
        this.reader = new BufferedInputStream(new FileInputStream(file), 15);
    }

    synchronized public byte[][] read (byte [] bytes, ArrayList<Byte> list) throws IOException, ArrayIndexOutOfBoundsException {

        int c;
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
            return new byte[][]{bytes, toByteArray(list)};
    }

    private byte [] toByteArray(ArrayList<Byte> list) {
        byte [] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }
}
