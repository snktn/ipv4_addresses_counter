package ru.snktn.ipv4AddressesCounter;

import java.util.ArrayList;

public interface IPv4AddressExtractor {

    default ArrayList<Integer> extract(byte[] bytes) {
        ArrayList<Integer> list = new ArrayList<>(bytes.length / 15);
        int[] ints = new int[4];
        int i = 3; //set last octet of address
        int f = 1; // place value of digit in the octet
        int octet = 0;
        bytes = reverse(bytes);
        for (int j = 0; j < bytes.length; j++) {
            int c = bytes[j];
            // if it's digit add it to octet number
            if (c >= 48 && c <= 57) {
                octet += (c - 48) * f;
                f *= 10;
            }
/*          if it's not digit and previous read is digit,
            converts all address's octets to integer and adds to the list*/
            else if ((j > 0 && bytes[j - 1] >= 48 && bytes[j - 1] <= 57 && (bytes[j] <= 48 || bytes[j] >= 57))) {
                ints[i] = octet;
                octet = 0;
                i--;
                f = 1;
                if (i == -1) {
                    i = 3;
                    list.add(intArrayToInt(ints));
                }
            }
            // if end of array reached converts all address's octets to integer and adds to the list
            if (j == bytes.length - 1){
                ints[i] = octet;
                octet = 0;
                i--;
                f = 1;
                if (i == -1) {
                    i = 3;
                    list.add(intArrayToInt(ints));
                }
            }
        }
        return list;
    }

    default byte[] reverse (byte[] array) {
        byte[] reversed = new byte[array.length];
        int i = array.length - 1;
        for (int j = 0; j < array.length; j++) {
            reversed[j] = array[i];
            i--;
        }
        return reversed;
    }

    default int intArrayToInt(int[] ints) {
        return ints[0] << 24 & -0x1000000 |
                (ints[1] <<16 & 0x00ff0000) |
                (ints[2] << 8 & 0x0000ff00) |
                (ints[3] & 0x000000ff);
    }

    // distributes indexes to corresponding lists
    default void distribute(ArrayList<Integer> decimalAddressesList, ArrayList<Integer>[][] indexLists, int nCounters) {
        for (int value : decimalAddressesList) {
            int n;
            int m;
            if (value < 0) n = 0;
            else n = 1;
            m = Math.abs(value % nCounters);
            indexLists[n][m].add(value / nCounters);
        }
    }

    // submit indexes list to corresponding counters
    default void submit (AddressCounter[][] addressCounters, ArrayList<Integer>[][] indexLists) {
        for (int i = 0; i < indexLists.length; i++) {
            for (int j = 0; j < indexLists[i].length; j++) {
                addressCounters[i][j].add(indexLists[i][j]);
            }
        }
    }
}