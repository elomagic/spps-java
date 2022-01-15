package de.elomagic.spps.shared;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class SppsPasswordCipherTest {

    SppsPasswordCipher cipher = new SppsPasswordCipher();

    @BeforeAll
    static void beforeAll() {
        SimpleCryptProvider provider = mock(SimpleCryptProvider.class, CALLS_REAL_METHODS);
        when(provider.encrypt(any(byte[].class))).thenAnswer(i -> "{" + new String(i.getArgument(0, byte[].class), StandardCharsets.UTF_8) + "}");
        when(provider.decrypt(any(String.class))).thenAnswer(i -> i.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));

        SimpleCryptFactory.setProvider(provider);
    }

    @Test
    void testEncrypt() {
        String mySecret = "MySecret";

        char[] result = cipher.encrypt(mySecret);

        Assertions.assertArrayEquals(("{" + mySecret + "}").toCharArray(), result);
    }

    @Test
    void testDecrypt() {
        String mySecret = "MySecret";

        String result = cipher.decrypt(mySecret.toCharArray());

        Assertions.assertEquals("{" + mySecret + "}", result);
    }


    @Test
    void testMetaInf() throws IOException {

        String className = IOUtils.resourceToString("/META-INF/org.apache.openejb.cipher.PasswordCipher/spps", StandardCharsets.UTF_8);

        Assertions.assertEquals(SppsPasswordCipher.class.getName(), className.trim());

    }

}