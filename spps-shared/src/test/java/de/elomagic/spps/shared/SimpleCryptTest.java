package de.elomagic.spps.shared;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Deprecated
class SimpleCryptTest {

    private Path createEmptyTempFile() throws IOException {
        System.out.println("createEmptyTempFile()");
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @BeforeAll
    static void beforeAll() {
        SimpleCryptFactory.setProvider((AbstractSimpleCryptProvider) null);
    }

    @Test
    void testIsEncryptedValue() {
        System.out.println("Running testIsEncryptedValue");
        Assertions.assertTrue(SimpleCrypt.isEncryptedValue("{abc}"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue("abc}"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue("{abc"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue("abc"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue(null));
    }

    @Test
    void testCreatePrivateKeyFile() throws Exception {
        System.out.println("Running testCreatePrivateKeyFile");
        Path file = createEmptyTempFile();

        SimpleCrypt.setSettingsFile(file);

        Assertions.assertThrows(SimpleCryptException.class, () -> SimpleCrypt.createPrivateKeyFile(null, null, false));

        Assertions.assertDoesNotThrow(() -> SimpleCrypt.createPrivateKeyFile(file, null, true));

        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            p.load(reader);
        }

        Assertions.assertEquals(3, p.keySet().size());
        Assertions.assertTrue(p.containsKey("created"));
        Assertions.assertTrue(p.containsKey("key"));
        Assertions.assertTrue(p.containsKey("relocation"));
    }

    @Test
    void testEncryptDecryptCalls() {
        System.out.println("Running testEncryptDecryptCalls");
        String secret = "mySecret";

        String encrypted = SimpleCrypt.encrypt(secret.getBytes(StandardCharsets.UTF_8));

        Assertions.assertNotEquals(secret, encrypted);

        String decrypted = new String(SimpleCrypt.decryptToChars(encrypted));

        Assertions.assertEquals(secret, decrypted);

        encrypted = SimpleCrypt.encrypt(secret.toCharArray());

        Assertions.assertEquals(secret, new String(SimpleCrypt.decryptToChars(encrypted)));
    }

}