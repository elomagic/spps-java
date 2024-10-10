package de.elomagic.spps.bc;

import de.elomagic.spps.shared.SimpleCryptException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class SimpleCryptBCTest {

    private static final String PRIVATE_KEY_FILENAME = "settings";
    private static final Path PRIVATE_KEY_FILE = Paths.get(System.getProperty("user.home"), ".spps", PRIVATE_KEY_FILENAME);

    private static String backup;

    private static final SimpleCryptBC sc = new SimpleCryptBC();

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        if (Files.exists(PRIVATE_KEY_FILE)) {
            backup = FileUtils.readFileToString(PRIVATE_KEY_FILE.toFile(), StandardCharsets.UTF_8);
        }

        Files.deleteIfExists(PRIVATE_KEY_FILE);
    }

    @AfterAll
    static void afterAll() throws Exception {
        Files.deleteIfExists(PRIVATE_KEY_FILE);

        if (backup != null) {
            FileUtils.write(PRIVATE_KEY_FILE.toFile(), backup, StandardCharsets.UTF_8);
        }
    }



    @Test
    void testInit() throws Exception {
        Path file = createEmptyTempFile();
        sc.setSettingsFile(file);
        Files.deleteIfExists(file);

        Assertions.assertFalse(sc.isInitialize());

        Assertions.assertTrue(sc.init());

        Assertions.assertTrue(sc.isInitialize());

        Assertions.assertFalse(sc.init());
    }

    @Test
    void testEncryptDecryptWithString() throws Exception {
        Path file = createEmptyTempFile();
        sc.createPrivateKeyFile(file, null,true);
        sc.setSettingsFile(file);

        String value = "secret";

        String encrypted = sc.encrypt(value.getBytes(StandardCharsets.UTF_8));

        Assertions.assertNotEquals(value, encrypted);
        Assertions.assertEquals(54, encrypted.length());

        String decrypted = new String(sc.decrypt(encrypted), StandardCharsets.UTF_8);

        Assertions.assertEquals(value, decrypted);

        String e1 = sc.encrypt(value.getBytes(StandardCharsets.UTF_8));
        String e2 = sc.encrypt(value.toCharArray());
        Assertions.assertNotEquals(e1, e2);

        Assertions.assertThrows(SimpleCryptException.class, () -> sc.decrypt("{bullshit}"));
    }

    @Test
    void testEncryptDecryptWithChars() {
        String value = "secretäöüß";

        char[] chars = value.toCharArray();

        String encrypted = sc.encrypt(chars.clone());

        Assertions.assertNotEquals(value, encrypted);

        char[] decryptedChars = sc.decryptToChars(encrypted);

        Assertions.assertArrayEquals(chars, decryptedChars);

        Assertions.assertNull(sc.encrypt((char[])null));
        Assertions.assertNull(sc.encrypt((byte[])null));
        Assertions.assertNull(sc.decrypt(null));
        Assertions.assertNull(sc.decrypt(null));
    }

    @Test
    void testDecrypt1() {
        Assertions.assertEquals("NOT_NULL", new String(sc.decrypt("NOT_NULL"), StandardCharsets.UTF_8));
        Assertions.assertEquals("", new String(sc.decrypt(""), StandardCharsets.UTF_8));
        Assertions.assertNull(sc.decrypt(null));
        Assertions.assertThrows(SimpleCryptException.class, ()-> sc.decrypt("{fake}"));
    }

    @Test
    void testSetSettingsFile() throws Exception {
        String value = "secretäöüß";
        Path file1 = createEmptyTempFile();
        sc.createPrivateKeyFile(file1, null, true);
        sc.setSettingsFile(file1);
        String encrypted1 = sc.encrypt(value.getBytes(StandardCharsets.UTF_8));
        Assertions.assertTrue(sc.isEncryptedValue(encrypted1));
        Assertions.assertEquals(value, new String(sc.decrypt(encrypted1), StandardCharsets.UTF_8));

        Path file2 = createEmptyTempFile();
        sc.setSettingsFile(file2);
        Assertions.assertThrows(SimpleCryptException.class, () -> sc.decrypt(encrypted1));

        sc.createPrivateKeyFile(file2, null,true);
        Assertions.assertTrue(Files.exists(file2));

        String encrypted2 = sc.encrypt(value.toCharArray());
        sc.setSettingsFile(null);
        Assertions.assertThrows(SimpleCryptException.class, () -> sc.decrypt(encrypted2));
    }

    @Test
    void testMetaInf() throws Exception {
        String className = IOUtils.resourceToString("/META-INF/de.elomagic.spps/provider", StandardCharsets.UTF_8).trim();
        Assertions.assertNotNull(Class.forName(className));
    }

}
