/*
 * Simple Password Protection Solution with Bouncy Castle
 *
 * Copyright © 2021-present Carsten Rambow (spps.dev@elomagic.de)
 *
 * This file is part of Simple Password Protection Solution with Bouncy Castle.
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
package de.elomagic.spps.bc;

import de.elomagic.spps.shared.AbstractSimpleCryptProvider;
import de.elomagic.spps.shared.SimpleCryptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Simple crypt tool class by using BouncyCastle framework.
 */
public final class SimpleCryptBC extends AbstractSimpleCryptProvider {

    private static final Logger LOGGER = LogManager.getLogger(SimpleCryptBC.class);
    private static final String ALGORITHM_AES = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /**
     * Creates a new random initialization vector.
     *
     * @return Returns the initialization vector but never null
     */
    @NotNull
    private IvParameterSpec createInitializationVector() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[PRIVATE_KEY_SIZE / 16];
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @Override
    @NotNull
    protected Key createPrivateKey() throws SimpleCryptException {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_AES);
            kg.init(PRIVATE_KEY_SIZE);
            return kg.generateKey();
        } catch (Exception ex) {
            throw new SimpleCryptException(ex.getMessage(), ex);
        }
    }

    /**
     * Creates a cipher.
     *
     * @param opmode The operation mode of this cipher (this is one of the following: {@code ENCRYPT_MODE} or {@code DECRYPT_MODE}
     * @param iv Initialization vector for first block
     * @return Returns cipher
     */
    @NotNull
    private Cipher createCypher(int opmode, @NotNull final IvParameterSpec iv) throws SimpleCryptException {
        try {
            Key key = new SecretKeySpec(readPrivateKey(), ALGORITHM_AES);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleProvider());
            cipher.init(opmode, key, iv);

            return cipher;
        } catch (Exception ex) {
            throw new SimpleCryptException(ex.getMessage(), ex);
        }
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

        if (!existsSettingsFile()) {
            createPrivateKeyFile(null, null, false);
        }

        try {
            IvParameterSpec iv = createInitializationVector();
            Cipher cipher = createCypher(Cipher.ENCRYPT_MODE, iv);
            byte[] encrypted = cipher.doFinal(decrypted);

            byte[] data = new byte[iv.getIV().length + encrypted.length];
            System.arraycopy(iv.getIV(), 0, data, 0, iv.getIV().length);
            System.arraycopy(encrypted, 0, data, iv.getIV().length, encrypted.length);

            return "{" + Base64.toBase64String(data) + "}";
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

            // Next IDE warning can be ignored because we need the IV from the encrypted string. We don't want to generate a new one.
            IvParameterSpec iv = new IvParameterSpec(encryptedBytes, 0, 16);

            Cipher cipher = createCypher(Cipher.DECRYPT_MODE, iv);
            return cipher.doFinal(encryptedBytes, 16, encryptedBytes.length-16);
        } catch(Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SimpleCryptException("Unable to decrypt data.", ex);
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
    public String decryptToString(@Nullable final String encryptedBase64) throws SimpleCryptException {
        return encryptedBase64 == null ? null : new String(decrypt(encryptedBase64), StandardCharsets.UTF_8);
    }

}
