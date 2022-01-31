package de.elomagic.spps.shared;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AbstractSimpleCryptProviderTest {

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @Test
    void testReadPrivateKey() throws Exception {
        SimpleCryptProviderMock provider = new SimpleCryptProviderMock();

        Path file = createEmptyTempFile();
        Files.deleteIfExists(file);

        provider.setSettingsFile(file);

        assertFalse(provider.isInitialize());

        assertThrows(SimpleCryptException.class, provider::callReadPrivateKey);

        Files.write(file, IOUtils.resourceToByteArray("/test-settings"));

        assertTrue(provider.isInitialize());

        byte[] original = Base64.getDecoder().decode("JH/ysWODIUHB99Kh5UxxdUhSXQ4cj45iVm9qRWCWsXo=");

        assertArrayEquals(original, provider.callReadPrivateKey());
    }

    @Test
    void testImportPrivateKey() {

        SimpleCryptProviderMock provider = new SimpleCryptProviderMock();

        assertThrows(SimpleCryptException.class, () -> provider.importPrivateKey(null, false));

    }
}