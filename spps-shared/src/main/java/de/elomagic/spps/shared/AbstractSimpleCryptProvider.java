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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSimpleCryptProvider implements SimpleCryptProvider {

    private static final Logger LOGGER = LogManager.getLogger(AbstractSimpleCryptProvider.class);
    protected static final String KEY_CREATED = "created";
    protected static final String KEY_KEY = "key";
    protected static final String RELOCATION_KEY = "relocation";
    protected static final int PRIVATE_KEY_SIZE = 256;
    protected static final Path DEFAULT_SETTINGS_FILE = Paths.get(System.getProperty("user.home"), ".spps", "settings");
    protected static final AtomicReference<Path> SETTINGS_FILE = new AtomicReference<>(DEFAULT_SETTINGS_FILE);

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * @param decrypted a non encrypted byte array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    public abstract String encrypt(final byte[] decrypted) throws SimpleCryptException;

    /**
     * Decrypt an encrypted, Base64 encoded data string.
     *
     * @param encryptedBase64 Base64 encoded data string, encapsulated with curly brackets.
     * @return The encrypted data as string.
     * @throws SimpleCryptException Thrown when unable to decrypt data .
     */
    @Nullable
    public abstract byte[] decrypt(@Nullable final String encryptedBase64) throws SimpleCryptException;

    /**
     * Checks if settings file exists.
     *
     * Settings file can be "${user.home}/.spps/settings" or an alternative file which set with {@link AbstractSimpleCryptProvider#setSettingsFile(Path)}
     *
     * @return Returns true if exists.
     */
    public final boolean isInitialize() {
        return Files.exists(SETTINGS_FILE.get());
    }

    /**
     * Set an alternative default settings file instead of default "${user.home}/.spps/settings".
     *
     * An application can use this feature to prevent sharing of the private key with other applications.
     *
     * @param file Alternative settings file or null to use the default file.
     */
    public final void setSettingsFile(@Nullable final Path file) {
        LOGGER.info("Changing default settings file to {}", SETTINGS_FILE.get());
        SETTINGS_FILE.set(file == null ? DEFAULT_SETTINGS_FILE : file);
    }

    /**
     * Writes a settings file.
     *
     * @param file File to write
     * @param privateKey Base64 encoded private key. Will be wiped after writen.
     * @param relocationFile When set then relocation file name will be written in the settinga file. Can be null.
     * @param force If file already exists and force is true then it will overwrite it otherwise it will fail with a
     *              {@link SimpleCryptException}.
     * @throws SimpleCryptException Thrown when unable to write file
     */
    private void writePrivateKeyFile(
            @NotNull final Path file,
            @Nullable final byte[] privateKey,
            @Nullable final Path relocationFile,
            boolean force) throws SimpleCryptException {
        try (SecureProperties properties = new SecureProperties()) {
            if (Files.notExists(file.getParent())) {
                Files.createDirectories(file.getParent());
            }

            if (Files.exists(file) && !force) {
                throw new SimpleCryptException("Private key file \"" + file+ "\" already exists. Use parameter \"-Force\" to overwrite it.");
            }

            LOGGER.info("Creating settings file");

            properties.setKey(KEY_KEY, privateKey);
            properties.setKey(RELOCATION_KEY, relocationFile == null ? null : relocationFile.toString().getBytes(StandardCharsets.UTF_8));
            properties.setKey(KEY_CREATED, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()).getBytes(StandardCharsets.UTF_8));

            properties.write(file);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException("Unable to write private key.", ex);
        }
    }

    /**
     * Creates a private key file.
     *
     * @param settingsFile File where the private key will be stored. If null then default file, which be stored in the
     *                     user folder, will be used.
     * @param relocationFile Alternative file where to write file with private key
     * @param force When true and private key file already exists then it will be overwritten otherwise an exception
     *              will be thrown
     * @return Returns the created and Base64 encoded private key
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    public final byte[] createPrivateKeyFile(@Nullable final Path settingsFile, @Nullable final Path relocationFile, boolean force) throws SimpleCryptException {
        try {
            Path file = settingsFile == null ? SETTINGS_FILE.get() : settingsFile;

            byte[] result;
            if (relocationFile == null || file.equals(relocationFile)) {
                result = createPrivateKey();

                writePrivateKeyFile(file, result, null, force);
                return result;
            } else {
                result = createPrivateKeyFile(relocationFile, null, force);
                writePrivateKeyFile(file, null, relocationFile, force);
            }

            LOGGER.info("Creating settings file");
            return result;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException("Unable to create private key.", ex);
        }
    }

    /**
     * Imports a private key and replace an existing private key in the setting file.
     *
     * @param encodedPrivateKey Base64 encoded private key.
     * @param force Force overwriting existing setting file. Otherwise, it will fail with an exception when already
     *              exists.
     * @throws SimpleCryptException Thrown when unable to import private key
     */
    public void importPrivateKey(final byte[] encodedPrivateKey, boolean force) throws SimpleCryptException {
        if (encodedPrivateKey == null) {
            throw new SimpleCryptException("Private key must be set");
        }

        try {
            writePrivateKeyFile(SETTINGS_FILE.get(), encodedPrivateKey, null, force);
        } finally {
            Arrays.fill(encodedPrivateKey, (byte)0);
        }
    }

    protected void wipe(byte[] secret) {
        if (secret == null) {
            return;
        }

        Arrays.fill(secret, (byte)0);
    }

    /**
     * Read private key from default location.
     *
     * @return Returns the private key as byte array.
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    @NotNull
    protected final byte[] readPrivateKey() throws SimpleCryptException {
        return readPrivateKey(SETTINGS_FILE.get());
    }

    /**
     * Read a private key.
     *
     * @param file File of the private key. When relocation in file is set then key will be read from there.
     * @return Returns the private key as byte array.
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    @NotNull
    protected final byte[] readPrivateKey(@NotNull final Path file) throws SimpleCryptException {
        try {
            if (Files.notExists(file)) {
                throw new FileNotFoundException("Unable to find settings file. At first you have to create a private key.");
            }

            try (SecureProperties properties = new SecureProperties()) {
                properties.read(file);
                if (properties.containsValue(RELOCATION_KEY)) {
                    return readPrivateKey(Paths.get(new String(properties.getValueAsBytes(RELOCATION_KEY), StandardCharsets.UTF_8)));
                } else {
                    byte[] key = properties.getValueAsBytes(KEY_KEY);
                    try {
                        if (key == null) {
                            throw new SimpleCryptException("No private key set.");
                        }
                        return Base64.getDecoder().decode(key);
                    } finally {
                        if (key != null) {
                            Arrays.fill(key, (byte) 0);
                        }
                    }
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
     * Settings file can be "${user.home}/.spps/settings" or an alternative file which set with {@link AbstractSimpleCryptProvider#setSettingsFile(Path)}
     *
     * @throws SimpleCryptException Thrown when unable to create private key file
     * @return Returns true when settings file was created and false when settings file already exist.
     */
    public final boolean init() throws SimpleCryptException {
        if (!isInitialize()) {
            createPrivateKeyFile(null, null, false);
            return true;
        }

        return false;
    }

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * Note: Input parameter will be wiped.
     *
     * @param decrypted a non encrypted char array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    public final String encrypt(final char[] decrypted) throws SimpleCryptException {
        return decrypted == null ? null : encrypt(UTF8.toByteArray(decrypted));
    }

    /**
     * Decrypt an encrypted, Base64 encoded data string.
     *
     * Note: Input parameter will be wiped.
     *
     * @param encryptedBase64 Base64 encoded data string, encapsulated with curly brackets.
     * @return The encrypted data as char array.
     * @throws SimpleCryptException Thrown when unable to decrypt data .
     */
    @Nullable
    public final char[] decryptToChars(@Nullable final String encryptedBase64) throws SimpleCryptException {
        return encryptedBase64 == null ? null : UTF8.toCharArray(decrypt(encryptedBase64));
    }

}
