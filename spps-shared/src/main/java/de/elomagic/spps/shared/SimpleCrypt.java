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

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Deprecated class.
 *
 * @deprecated This class is deprecated since version 2.0 and will be removed at the latest with version 3.0.
 *             Please use {@link SimpleCryptFactory#getInstance()} instead.
 * @see SimpleCryptFactory#getInstance()
 */
@Deprecated
public class SimpleCrypt {

    private SimpleCrypt() {
    }

    /**
     * Returns true when value is encrypted, tagged by surrounding braces "{" and "}".
     *
     * @param value Value to be checked
     * @return Returns true when value is identified as an encrypted value.
     */
    static boolean isEncryptedValue(@Nullable final String value) {
        return SimpleCryptFactory.getInstance().isEncryptedValue(value);
    }

    /**
     * Set an alternative default settings file instead of default "${user.home}/.spps/settings".
     *
     * An application can use this feature to prevent sharing of the private key with other applications.
     *
     * @param file Alternative settings file or null to use the default file.
     */
    static void setSettingsFile(@Nullable Path file) {
        SimpleCryptFactory.getInstance().setSettingsFile(file);
    }

    /**
     * Creates a private key file.
     *
     * @param settingsFile File where the private key will be stored. If null then default file, which be stored in the user folder, will be used.
     * @param relocationFile Alternative file where to write file with private key
     * @param force When true and private key file already exists then it will be overwritten otherwise an exception will be thrown
     * @throws SimpleCryptException Thrown when unable to create private key
     */
    static void createPrivateKeyFile(@Nullable final Path settingsFile, @Nullable final Path relocationFile, boolean force) throws SimpleCryptException {
        SimpleCryptFactory.getInstance().createPrivateKeyFile(settingsFile, relocationFile, force);
    }

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * @param decrypted a non encrypted byte array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    static String encrypt(final byte[] decrypted) throws SimpleCryptException {
        return SimpleCryptFactory.getInstance().encrypt(decrypted);
    }

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * @param decrypted a non encrypted char array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    static String encrypt(final char[] decrypted) throws SimpleCryptException {
        return SimpleCryptFactory.getInstance().encrypt(decrypted);
    }

    /**
     * Decrypt an encrypted, Base64 encoded data string.
     *
     * @param encryptedBase64 Base64 encoded data string, encapsulated with curly brackets.
     * @return The encrypted data as char array.
     * @throws SimpleCryptException Thrown when unable to decrypt data .
     */
    @Nullable
    static char[] decryptToChars(@Nullable final String encryptedBase64) throws SimpleCryptException {
        return SimpleCryptFactory.getInstance().decryptToChars(encryptedBase64);
    }

}
