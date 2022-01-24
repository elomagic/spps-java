package de.elomagic.spps.shared;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

class SimpleCryptCommandToolTest {

    private final SimpleCryptCommandTool sc = new SimpleCryptCommandTool();

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-Temp-", ".txt").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

    @BeforeAll
    static void beforeALl() {
        SimpleCryptFactory.setProvider(SimpleCryptProviderMock.class);
    }

    @Test
    void testHasArgumentForOption() {
        Assertions.assertFalse(sc.hasArgumentForOption(Collections.emptyList(), ""));
        Assertions.assertFalse(sc.hasArgumentForOption(Collections.emptyList(), "-Secret"));
        Assertions.assertFalse(sc.hasArgumentForOption(Collections.emptyList(), "-Test"));
        Assertions.assertFalse(sc.hasArgumentForOption(Arrays.asList("test", "test", "-Secret"), "-Secret"));
        Assertions.assertTrue(sc.hasArgumentForOption(Arrays.asList("test", "test", "-Secret", "mySecret"), "-Secret"));
    }

    @Test
    void testGetArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> sc.getArgument(Collections.emptyList(), null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sc.getArgument(Collections.emptyList(), "test"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sc.getArgument(Collections.singletonList("test"), "test"));
        Assertions.assertEquals("value", sc.getArgument(Arrays.asList("test", "value"), "test"));
    }

    @Test
    void testRun() {
        Assertions.assertEquals(0, sc.run(new String[] {"abcde"}));
        Assertions.assertEquals(0, sc.run(null));
        Assertions.assertEquals(1, sc.run(new String[] {"-Secret"}));
    }

    @Test
    void testCreatePrivateKey() throws Exception {
        Path file = createEmptyTempFile();

        try (PrintStreamMock out = new PrintStreamMock()) {
            Assertions.assertEquals(1, sc.run(new String[]{"-CreatePrivateKey", "-File", file.toString()}));
            Assertions.assertTrue(out.toString().isEmpty());

            Assertions.assertEquals(0, sc.run(new String[]{"-CreatePrivateKey", "-Force", "-Print", "-File", file.toString()}));

            String base64 = out.toString().trim();
            String decrypted = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            Assertions.assertEquals("ThisIsAPrivateKeyMock", decrypted);

            SecureProperties p1 = new SecureProperties();
            p1.read(file);

            Assertions.assertEquals(3, p1.size());
            Assertions.assertTrue(p1.containsValue("created"));
            Assertions.assertTrue(p1.containsValue("key"));
            Assertions.assertFalse(p1.containsValue("relocation"));

            out.reset();

            Assertions.assertEquals(0, sc.run(new String[]{"-CreatePrivateKey", "-PreventWrite", "-File", file.toString()}));
            base64 = out.toString().trim();
            decrypted = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            Assertions.assertEquals("ThisIsAPrivateKeyMock", decrypted);

            SecureProperties p2 = new SecureProperties();
            p2.read(file);

            Assertions.assertArrayEquals(p1.getValueAsBytes("created"), p2.getValueAsBytes("created"));
        }
    }

    @Test
    void testImport() throws Exception {

        byte[] privateKey = Base64.getEncoder().encode("AnotherTestPrivateKey".getBytes(StandardCharsets.UTF_8));

        Path alternateKeyFile = createEmptyTempFile();
        Path importFile = createEmptyTempFile();
        Files.write(importFile, privateKey);

        SimpleCryptFactory.getInstance().setSettingsFile(alternateKeyFile);

        Assertions.assertTrue(Files.exists(alternateKeyFile), "Settings file not created");

        Assertions.assertEquals(1, sc.run(new String[]{"-ImportPrivateKey", "-File", importFile.toString()}));
        Assertions.assertFalse(Arrays.equals(privateKey, Files.readAllBytes(alternateKeyFile)));

        Assertions.assertEquals(0, sc.run(new String[]{"-ImportPrivateKey", "-Force", "-File", importFile.toString()}));

        SecureProperties properties = new SecureProperties();
        properties.read(alternateKeyFile);

        Assertions.assertArrayEquals(privateKey, properties.getValueAsBytes("key"));

    }

}