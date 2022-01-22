package de.elomagic.spps.shared;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

class SimpleCryptProviderTest {

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @BeforeAll
    static void beforeAll() {
        SimpleCryptFactory.setProvider(SimpleCryptProviderMock.class);
    }

    @Test
    void testIsEncryptedValue() {
        SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        Assertions.assertTrue(provider.isEncryptedValue("{abc}"));
        Assertions.assertFalse(provider.isEncryptedValue("abc}"));
        Assertions.assertFalse(provider.isEncryptedValue("{abc"));
        Assertions.assertFalse(provider.isEncryptedValue("abc"));
        Assertions.assertFalse(provider.isEncryptedValue(null));
    }

    @Test
    void testCreatePrivateKeyFile() throws Exception {
        Path file = createEmptyTempFile();
        SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        Assertions.assertThrows(SimpleCryptException.class, () -> provider.createPrivateKeyFile(file, null, false));

        Assertions.assertDoesNotThrow(() -> provider.createPrivateKeyFile(file, null, true));

        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            p.load(reader);
        }

        Assertions.assertEquals(3, p.keySet().size());
        Assertions.assertTrue(p.keySet().contains("created"));
        Assertions.assertTrue(p.keySet().contains("key"));
        Assertions.assertTrue(p.keySet().contains("relocation"));
    }

}