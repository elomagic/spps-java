package de.elomagic.spps.shared;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class TestDummySppsProviderTest {

    private final TestDummySppsProvider provider = new TestDummySppsProvider();

    @Test
    void testCreatePrivateKey() {
        assertArrayEquals(new byte[0], provider.createPrivateKey());
    }

    @Test
    void testCreatePrivateKeyFile() {
        assertArrayEquals(new byte[0], provider.createPrivateKeyFile(null, null, false));
    }

    @Test
    void testEncryptBytes() {
        String pseudoSecret = "pseudoSecret";

        String s = provider.encrypt(pseudoSecret.getBytes(StandardCharsets.UTF_8));

        assertTrue(provider.isEncryptedValue(s));
        assertEquals(pseudoSecret, String.valueOf(provider.decryptToChars(s)));
        assertNull(provider.decryptToChars(null));
        assertNull(provider.encrypt((byte[])null));
    }

    @Test
    void testEncryptChars() {
        String pseudoSecret = "pseudoSecret";

        String s = provider.encrypt(pseudoSecret.toCharArray());

        assertTrue(provider.isEncryptedValue(s));
        assertEquals(pseudoSecret, String.valueOf(provider.decryptToChars(s)));
        assertNull(provider.decryptToChars(null));
        assertNull(provider.encrypt((char[])null));
    }

}