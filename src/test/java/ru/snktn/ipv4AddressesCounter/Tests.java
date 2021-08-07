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


public class Tests {

    @Test
    void shortTest () {
            test(3);
    }

    @Test
    void longTest () {
            test(100000);
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
