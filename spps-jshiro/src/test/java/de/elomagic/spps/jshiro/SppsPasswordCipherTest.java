package de.elomagic.spps.jshiro;

import de.elomagic.spps.shared.SimpleCryptFactory;
import de.elomagic.spps.shared.SppsPasswordCipher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class SppsPasswordCipherTest {

    private static final SimpleCryptShiro sc = new SimpleCryptShiro();

    @BeforeAll
    static void beforeAll() {
        SimpleCryptFactory.setProvider(SimpleCryptShiro.class);
    }

    @Test
    void testEncrypt() {

        SppsPasswordCipher cipher = new SppsPasswordCipher();

        String secret = "MyTestSecret";

        char[] encryptedSecret = cipher.encrypt("MyTestSecret");

        Assertions.assertEquals(secret, new String(sc.decrypt(String.valueOf(encryptedSecret)), StandardCharsets.UTF_8));
        Assertions.assertNull(cipher.encrypt(null));

    }

    @Test
    void testDecrypt() {

        SppsPasswordCipher cipher = new SppsPasswordCipher();

        String secret = "MyTestSecret";

        char[] encryptedSecret = sc.encrypt(secret.getBytes(StandardCharsets.UTF_8)).toCharArray();

        Assertions.assertEquals(secret, cipher.decrypt(encryptedSecret));
        Assertions.assertEquals(secret, cipher.decrypt(Arrays.copyOfRange(encryptedSecret, 1, encryptedSecret.length-1)));
        Assertions.assertNull(cipher.decrypt(null));

    }

}
