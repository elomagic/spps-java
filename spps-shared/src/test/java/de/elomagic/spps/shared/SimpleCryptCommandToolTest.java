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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

class SimpleCryptCommandToolTest {

    private final SimpleCryptCommandTool sc = new SimpleCryptCommandTool();

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-", ".tmp").toPath();
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
        }

        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            p.load(reader);
        }

        Assertions.assertEquals(3, p.keySet().size());
        Assertions.assertTrue(p.keySet().contains("created"));
        Assertions.assertTrue(p.keySet().contains("key"));
        Assertions.assertTrue(p.keySet().contains("relocation"));
    }

    @Test
    void testImport() throws Exception {

        // Create private key if doesn't exists
        //sc.run(new String[]{"-ImportPrivateKey", "-File", file.toString()})

        //Path file = createEmptyTempFile();
        //Assertions.assertEquals(1, sc.run(new String[]{"-ImportPrivateKey", "-File", file.toString()}));

        // TODO Test Import
    }

}