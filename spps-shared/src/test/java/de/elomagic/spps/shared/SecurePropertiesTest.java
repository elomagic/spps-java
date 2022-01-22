package de.elomagic.spps.shared;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SecurePropertiesTest {

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @Test
    void close() {
        SecureProperties properties = new SecureProperties();
        properties.setKey("abc", SecureRandom.getSeed(10));

        assertTrue(properties.containsValue("abc"));

        properties.close();

        assertFalse(properties.containsValue("abc"));
    }

    @Test
    void read() throws IOException {
        byte[] data1 = Base64.getEncoder().encode(SecureRandom.getSeed(10));
        byte[] data2 = "TestAÄßData".getBytes(StandardCharsets.UTF_8);

        Path testFile = createEmptyTempFile();

        try (Writer writer = Files.newBufferedWriter(testFile, StandardCharsets.UTF_8)) {
            writer.write("# Test Comment\n");
            writer.write("abc=" + new String(data1, StandardCharsets.UTF_8) + "\n");
            writer.write("xyz=" + new String(data2, StandardCharsets.UTF_8) + "\n");

            writer.flush();
        }

        SecureProperties secureProperties = new SecureProperties();
        secureProperties.read(testFile);

        assertEquals(2, secureProperties.size());
        assertArrayEquals(data1, secureProperties.getValueAsBytes("abc"));
        assertArrayEquals(data2, secureProperties.getValueAsBytes("xyz"));
    }

    @Test
    void containsValue() {
        SecureProperties properties = new SecureProperties();

        assertFalse(properties.containsValue("abc"));

        properties.setKey("abc", SecureRandom.getSeed(10));

        assertTrue(properties.containsValue("abc"));
    }

    @Test
    void setKey() {
        byte[] data = SecureRandom.getSeed(10);
        SecureProperties properties = new SecureProperties();

        assertFalse(properties.containsValue("abc"));

        properties.setKey("abc", data);

        assertTrue(properties.containsValue("abc"));
        assertArrayEquals(data, properties.getValueAsBytes("abc"));

        properties.setKey("abc", null);

        assertFalse(properties.containsValue("abc"));
    }

    @Test
    void write() throws IOException {
        byte[] data1 = Base64.getEncoder().encode(SecureRandom.getSeed(10));
        byte[] data2 = "TestÖÄßData".getBytes(StandardCharsets.UTF_8);

        Path testFile = createEmptyTempFile();

        SecureProperties secureProperties = new SecureProperties();
        secureProperties.setKey("abc", data1);
        secureProperties.setKey("xyz", data2);
        secureProperties.write(testFile);

        Properties properties = new Properties();
        properties.load(Files.newBufferedReader(testFile));

        assertEquals(2, properties.size());
        assertArrayEquals(data1, properties.getProperty("abc").getBytes(StandardCharsets.UTF_8));
        assertArrayEquals(data2, properties.getProperty("xyz").getBytes(StandardCharsets.UTF_8));

    }
}