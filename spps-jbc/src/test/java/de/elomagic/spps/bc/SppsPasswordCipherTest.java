package de.elomagic.spps.bc;

import de.elomagic.spps.shared.SimpleCryptFactory;
import de.elomagic.spps.shared.SppsPasswordCipher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SppsPasswordCipherTest {

    private static final SimpleCryptBC sc = new SimpleCryptBC();

    @BeforeAll
    static void beforeAll() {
        SimpleCryptFactory.setProvider(SimpleCryptBC.class);
    }

    @Test
    void testEncrypt() {

        SppsPasswordCipher cipher = new SppsPasswordCipher();

        String secret = "MyTestSecret";

        char[] encryptedSecret = cipher.encrypt("MyTestSecret");

        Assertions.assertEquals(secret, sc.decryptToString(String.valueOf(encryptedSecret)));
        Assertions.assertNull(cipher.encrypt(null));

    }

    @Test
    void testDecrypt() {

        SppsPasswordCipher cipher = new SppsPasswordCipher();

        String secret = "MyTestSecret";

        char[] encryptedSecret = sc.encrypt(secret).toCharArray();

        Assertions.assertEquals(secret, cipher.decrypt(encryptedSecret));
        Assertions.assertEquals(secret, cipher.decrypt(Arrays.copyOfRange(encryptedSecret, 1, encryptedSecret.length-1)));
        Assertions.assertNull(cipher.decrypt(null));

    }

}
