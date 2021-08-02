package ru.snktn.ipv4AddressesCounter;

import java.io.*;
import java.util.ArrayList;

public class Reader {
    private final BufferedInputStream reader;
    private boolean hasNext = true;
    public boolean hasNext() {
        return hasNext;
    }

    public Reader(File file) throws FileNotFoundException {
        this.reader = new BufferedInputStream(new FileInputStream(file), Parser.CHUNK_SIZE + 15);
    }

    synchronized public byte[][] read (byte [] bytes, ArrayList<Byte> list) throws IOException, ArrayIndexOutOfBoundsException {

        byte[][] arrays;
        if (reader.available() <= Parser.CHUNK_SIZE) {
            AddressCounter.shutdown();
            hasNext = false;
            byte [] lastBytes = new byte[reader.available()];
            reader.read(lastBytes);
            arrays = new byte[][]{lastBytes};
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
