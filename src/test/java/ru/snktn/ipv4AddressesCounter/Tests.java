package ru.snktn.ipv4AddressesCounter;

import org.junit.jupiter.api.Test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;


class Tests {

    Random random = new Random();

    @Test
    void test () {
        for (int i = 0; i < 100; i++) {
            int n = random.nextInt(3) + 1;
            test(n);
        }
        for (int i = 0; i < 100; i++) {
            int n = random.nextInt(17000) + 1;
            test(n);
        }
        for (int i = 0; i < 10; i++) {
            int n = random.nextInt(1000000) + 1;
            test(n);
        }
    }

    private void test (int n) {
        try {
            File testFile = generate(n);
            int result = Main.countUniqueAddresses(testFile);
            assertEquals(n, result);
            if (!testFile.delete()) testFile.deleteOnExit();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private File generate (int n) throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        String fs = File.separator;
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File testFile = new File(tmpDir + fs + timeStamp + ".tmp");
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
        HashSet<InetAddress> set = new HashSet<>();
        Random random = new Random();
        while (set.size() < n) {
            byte[] bytes = new byte [4];
            random.nextBytes(bytes);
            InetAddress address = Inet4Address.getByAddress(bytes);
            set.add(address);
            writer.write(address.getHostAddress());
            writer.newLine();
        }
        writer.close();
        return testFile;
    }
}
