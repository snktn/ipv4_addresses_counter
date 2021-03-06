package ru.snktn.ipv4AddressesCounter;

public interface IPv4AddressExtractor {

    default int [] extract(byte[] bytes) {
        int [] arr = new int[bytes.length / 7];
        int k = 0;
        int[] ints = new int[4];
        int i = 3; //set last octet of address
        int f = 1; // place value of digit in the octet
        int octet = 0;
        for (int j = 0; j < bytes.length; j++) {
            byte c = bytes[j];
            // if it's digit add it to octet number
            if (c >= 48 && c <= 57) {
                octet += (c - 48) * f;
                f *= 10;
            }
/*          if it's not digit and previous read is digit,
            converts all address's octets to integer and adds to the array*/
            else if ((j > 0 && bytes[j - 1] >= 48 && bytes[j - 1] <= 57 && (bytes[j] <= 48 || bytes[j] >= 57))) {
                ints[i] = octet;
                octet = 0;
                i--;
                f = 1;
                if (i == -1) {
                    i = 3;
                    try {
                        arr[k] = intArrayToInt(ints);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    k++;
                }
            }
            // if end of array reached converts all address's octets to integer and adds to the array
            if (j == bytes.length - 1){
                ints[i] = octet;
                octet = 0;
                i--;
                f = 1;
                if (i == -1) {
                    i = 3;
                    try {
                        arr[k] = intArrayToInt(ints);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    k++;
                }
            }
        }
        return arr;
    }

    default int intArrayToInt(int[] ints) {
        return ints[0] << 24 & -0x1000000 |
                (ints[1] <<16 & 0x00ff0000) |
                (ints[2] << 8 & 0x0000ff00) |
                (ints[3] & 0x000000ff);
    }
}