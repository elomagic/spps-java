package de.elomagic.spps.shared;

import org.junit.jupiter.api.Assertions;
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
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @Test
    void testIsEncryptedValue() {
        Assertions.assertTrue(SimpleCrypt.isEncryptedValue("{abc}"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue("abc}"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue("{abc"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue("abc"));
        Assertions.assertFalse(SimpleCrypt.isEncryptedValue(null));
    }

    @Test
    void testCreatePrivateKeyFile() throws Exception {
        Path file = createEmptyTempFile();

        Assertions.assertThrows(SimpleCryptException.class, () -> SimpleCrypt.createPrivateKeyFile(file, null, false));

        Assertions.assertDoesNotThrow(() -> SimpleCrypt.createPrivateKeyFile(file, null, true));

        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            p.load(reader);
        }

        Assertions.assertEquals(2, p.keySet().size());
    }

    @Test
    void testEncryptDecryptCalls() {
        String secret = "mySecret";

        String encrypted = SimpleCrypt.encrypt(secret.getBytes(StandardCharsets.UTF_8));

        Assertions.assertNotEquals(secret, encrypted);

        String decrypted = new String(SimpleCrypt.decryptToChars(encrypted));

        Assertions.assertEquals(secret, decrypted);
    }

}