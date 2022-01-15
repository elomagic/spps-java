/*
 * Simple Password Protection Solution Shared Classes
 *
 * Copyright © 2021-present Carsten Rambow (spps.dev@elomagic.de)
 *
 * This file is part of Simple Password Protection Solution Shared Classes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.elomagic.spps.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleCryptProvider {

    private static final Logger LOGGER = LogManager.getLogger(SimpleCryptProvider.class);
    protected static final String KEY_KEY = "key";
    protected static final String RELOCATION_KEY = "relocation";
    protected static final int PRIVATE_KEY_SIZE = 256;
    protected static final Path DEFAULT_SETTINGS_FILE = Paths.get(System.getProperty("user.home"), ".spps", "settings");
    protected static final AtomicReference<Path> SETTINGS_FILE = new AtomicReference<>(DEFAULT_SETTINGS_FILE);

    // TODO JavaDoc
    protected abstract Key createPrivateKey() throws SimpleCryptException;

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * @param decrypted a non encrypted byte array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    public abstract String encrypt(byte[] decrypted) throws SimpleCryptException;

    /**
     * Decrypt an encrypted, Base64 encoded data string.
     *
     * @param encryptedBase64 Base64 encoded data string, encapsulated with curly brackets.
     * @return The encrypted data as string.
     * @throws SimpleCryptException Thrown when unable to decrypt data .
     */
    @Nullable
    public abstract byte[] decrypt(@Nullable String encryptedBase64) throws SimpleCryptException;

    /**
     * Check is the settings file exists.
     *
     * @return Returns true when exists otherwise false
     */
    protected boolean existsSettingsFile() {
        return Files.exists(SETTINGS_FILE.get());
    }

    /**
     * Checks if settings file exists.
     *
     * Settings file can be "${user.home}/.spps/settings" or an alternative file which set with {@link SimpleCryptProvider#setSettingsFile(Path)}
     *
     * @return Returns true if exists.
     */
    public boolean isInitialize() {
        return Files.exists(SETTINGS_FILE.get());
    }

    /**
     * Set an alternative default settings file instead of default "${user.home}/.spps/settings".
     *
     * An application can use this feature to prevent sharing of the private key with other applications.
     *
     * @param file Alternative settings file or null to use the default file.
     */
    public void setSettingsFile(@Nullable Path file) {
        LOGGER.info("Changing default settings file to {}", SETTINGS_FILE.get());
        SETTINGS_FILE.set(file == null ? DEFAULT_SETTINGS_FILE : file);
    }

    /**
     * Creates a private key file.
     *
     * @param file File where the private key will be stored. If null then default file, which be stored in the user folder, will be used.
     * @param relocationFile Alternative file where to write file with private key
     * @param force When true and private key file already exists then it will be overwritten otherwise an exception will be thrown
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    public void createPrivateKeyFile(@Nullable Path file, @Nullable Path relocationFile, boolean force) throws SimpleCryptException {
        try {
            file = file == null ? SETTINGS_FILE.get() : file;

            if (Files.notExists(file.getParent())) {
                Files.createDirectories(file.getParent());
            }

            if (Files.exists(file) && !force) {
                throw new SimpleCryptException("Private key file \"" + file+ "\" already exists. Use parameter \"-Force\" to overwrite it.");
            }

            Properties p = new Properties();

            if (relocationFile == null || file.equals(relocationFile)) {
                Key key = createPrivateKey();
                byte[] result = key.getEncoded();

                String base64 = Base64.getEncoder().encodeToString(result);

                p.put(KEY_KEY, base64);
                p.put(RELOCATION_KEY, "");
            } else {
                p.put(KEY_KEY, "");
                p.put(RELOCATION_KEY, relocationFile.toString());
                createPrivateKeyFile(relocationFile, null, force);
            }

            LOGGER.info("Creating settings file");

            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                p.store(writer, "SPPS Settings");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IllegalStateException("Unable to create private key", ex);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException("Unable to create or read private key.", ex);
        }
    }

    /**
     * Read private key from default location.
     *
     * @return Returns the private key as byte array.
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    @NotNull
    protected byte[] readPrivateKey() throws SimpleCryptException {
        return readPrivateKey(SETTINGS_FILE.get());
    }

    /**
     * Returns true when value is encrypted, tagged by surrounding braces "{" and "}".
     *
     * @param value Value to be checked
     * @return Returns true when value is identified as an encrypted value.
     */
    public boolean isEncryptedValue(@Nullable String value) {
        return value != null && value.startsWith("{") && value.endsWith("}");
    }

    /**
     * Read a private key.
     *
     * @param file File of the private key. When relocation in file is set then key will be read from there.
     * @return Returns the private key as byte array.
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    @NotNull
    protected byte[] readPrivateKey(@NotNull Path file) throws SimpleCryptException {
        try {
            if (Files.notExists(file)) {
                throw new FileNotFoundException("Unable to find settings file. At first you have to create a private key.");
            }

            Properties p = new Properties();
            try (Reader reader = Files.newBufferedReader(file)) {
                p.load(reader);

                if (p.getProperty(RELOCATION_KEY, "").trim().length() != 0) {
                    return readPrivateKey(Paths.get(p.getProperty(RELOCATION_KEY)));
                } else {
                    String key = p.getProperty(KEY_KEY, "");
                    if ("".equals(key)) {
                        throw new SimpleCryptException("No private key set.");
                    }
                    return Base64.getDecoder().decode(key);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException("Unable to read private key.", ex);
        }
    }

    /**
     * Initialize SimpleCrypt by checking of setting file.
     * <p>
     * Settings file can be "${user.home}/.spps/settings" or an alternative file which set with {@link SimpleCryptProvider#setSettingsFile(Path)}
     *
     * @throws SimpleCryptException Thrown when unable to create private key file
     * @return Returns true when settings file was created and false when settings file already exist.
     */
    public boolean init() throws SimpleCryptException {
        if (!isInitialize()) {
            createPrivateKeyFile(null, null, false);
            return true;
        }

        return false;
    }

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * @param decrypted a non encrypted char array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    public String encrypt(char[] decrypted) throws SimpleCryptException {
        // TODO Safe cast of char array to byte array
        return decrypted == null ? null : encrypt(new String(decrypted).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decrypt an encrypted, Base64 encoded data string.
     *
     * @param encryptedBase64 Base64 encoded data string, encapsulated with curly brackets.
     * @return The encrypted data as char array.
     * @throws SimpleCryptException Thrown when unable to decrypt data .
     */
    @Nullable
    public char[] decryptToChars(@Nullable String encryptedBase64) throws SimpleCryptException {
        return encryptedBase64 == null ? null : new String(decrypt(encryptedBase64), StandardCharsets.UTF_8).toCharArray();
    }

}
