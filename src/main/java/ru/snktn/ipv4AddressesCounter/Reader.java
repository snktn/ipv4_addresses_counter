package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.ArrayList;


class Reader {
    private final BufferedInputStream reader;
    private volatile boolean hasNext = true;
    synchronized boolean hasNext() {
        return hasNext;
    }

    Reader(BufferedInputStream bufferedInputStream) {
        this.reader = bufferedInputStream;
    }

    synchronized public byte[][] read (byte [] bytes, ArrayList<Byte> list) throws IOException, ArrayIndexOutOfBoundsException {
        byte[][] arrays;
        if (reader.available() <= Parser.CHUNK_SIZE) {
            hasNext = false;
            byte [] lastBytes = new byte[reader.available()];
            reader.read(lastBytes);
            arrays = new byte[][]{lastBytes};
            AddressCounter.shutdown();
        }
        else {
            int c;
            reader.read(bytes);
            c = bytes[bytes.length - 1];
            while (c == 46 || c >= 48 && c <= 57 ){
                c = reader.read();
                list.add((byte) c );
            }
            arrays = new byte[][]{bytes, toByteArray(list)};
        }
        return arrays;
    }

    private byte [] toByteArray(ArrayList<Byte> list) {
        byte [] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }
}
