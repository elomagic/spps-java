/*
 * Simple Password Protection Solution with Apache Shiro
 *
 * Copyright © 2021-present Carsten Rambow (spps.dev@elomagic.de)
 *
 * This file is part of Simple Password Protection Solution with Apache Shiro.
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
package de.elomagic.spps.jshiro;

import de.elomagic.spps.shared.AbstractSimpleCryptProvider;
import de.elomagic.spps.shared.SimpleCryptException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.DefaultBlockCipherService;
import org.apache.shiro.util.ByteSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * Simple crypt tool class by using Apache Shiro framework.
 */
public final class SimpleCryptShiro extends AbstractSimpleCryptProvider {

    private static final Logger LOGGER = LogManager.getLogger(SimpleCryptShiro.class);
    private static final DefaultBlockCipherService CIPHER = new AesCipherService();

    /**
     * Creates a random AES key with 256 ke size.
     *
     * @return Returns the non Base64 encoded key
     * @throws SimpleCryptException Thrown when something went wring on creation of the key.
     */
    @Override
    @NotNull
    public byte[] createPrivateKey() {
        return Base64.encode(CIPHER.generateNewKey(PRIVATE_KEY_SIZE).getEncoded());
    }

    /**
     * Encrypt, encoded as Base64 and encapsulate with curly bracket of a string.
     *
     * @param decrypted a non encrypted byte array
     * @return Returns an encrypted, Base64 encoded string, surrounded with curly brackets.
     * @throws SimpleCryptException Thrown when an error occurred during encrypting.
     */
    @Nullable
    @Override
    public String encrypt(final byte[] decrypted) throws SimpleCryptException {
        if (decrypted == null) {
            return null;
        }

        if (!isInitialize()) {
            createPrivateKeyFile(null, null, false);
        }

        try {
            ByteSource encrypted = CIPHER.encrypt(decrypted, readPrivateKey());

            return "{" + encrypted.toBase64() + "}";
        } catch(Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException(ex.getMessage(), ex);
        }
    }

    /**
     * Decrypt an encrypted, Base64 encoded data string.
     *
     * @param encryptedBase64 Base64 encoded data string, encapsulated with curly brackets.
     * @return The encrypted data as string.
     * @throws SimpleCryptException Thrown when unable to decrypt data .
     */
    @Nullable
    @Override
    public byte[] decrypt(@Nullable final String encryptedBase64) throws SimpleCryptException {
        if(!isEncryptedValue(encryptedBase64)) {
            return encryptedBase64 == null ? null : encryptedBase64.getBytes(StandardCharsets.UTF_8);
        }

        try {
            byte[] encryptedBytes = Base64.decode(encryptedBase64.substring(1, encryptedBase64.length() - 1));

            ByteSource decrypted = CIPHER.decrypt(encryptedBytes, readPrivateKey());

            return decrypted.getBytes();
        } catch(Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException("Unable to decrypt data.", ex);
        }
    }

}
