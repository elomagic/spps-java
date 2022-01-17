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
import org.apache.openejb.cipher.PasswordCipher;

import java.nio.charset.StandardCharsets;

/**
 * Password cipher implementation for Apache Tomee.
 *
 * See also @see https://tomee.apache.org/latest/examples/datasource-ciphered-password.html
 */
public class SppsPasswordCipher implements PasswordCipher {

    private static final Logger LOGGER = LogManager.getLogger(SppsPasswordCipher.class);

    /**
     * Encodes a given plain text password and returns the encoded password.
     *
     * @param secret The password to encode. May not be null, nor empty.
     * @return The encoded password.
     */
    @Override
    public char[] encrypt(String secret) {
        LOGGER.debug("Encrypt secret into SPPS encrypted secret");

        return secret == null ? null : SimpleCryptFactory
                .getInstance()
                .encrypt(secret.getBytes(StandardCharsets.UTF_8))
                .toCharArray();
    }

    /**
     * Decodes an encoded password and returns a plain text password.
     *
     * @param chars The ciphered password to decode. May not be <code>null</code>, nor empty.
     * @return The plain text password.
     */
    @Override
    public String decrypt(char[] chars) {
        LOGGER.debug("Decrypt SPPS encrypted password");

        if (chars == null) {
            return null;
        }

        String encryptedSecret = String.valueOf(chars);

        // For some unknown reason, Tomee removes the closing bracket from the encrypted SPPS secret,
        // so we have to add the bracket ourselves
        encryptedSecret = SimpleCryptFactory.getInstance().isEncryptedValue(encryptedSecret) ? encryptedSecret : ("{" + encryptedSecret + "}");

        return String.valueOf(SimpleCryptFactory.getInstance().decryptToChars(encryptedSecret));
    }

}
