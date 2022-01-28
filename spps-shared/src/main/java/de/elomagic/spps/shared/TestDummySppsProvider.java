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

import java.nio.file.Path;
import java.util.Base64;

/**
 * Class written for testing purposes only, e.g. to simplify JUnit tests without using of private keys.
 *
 * <b>DO NOT USE THIS CLASS IN A PRODUCTIVE ENVIRONMENT !</b>
 */
public final class TestDummySppsProvider implements SimpleCryptProvider {

    private static final Logger LOGGER = LogManager.getLogger(TestDummySppsProvider.class);

    public TestDummySppsProvider() {
        LOGGER.warn("DO NOT USE THE TestDummySppsProvider CLASS IN A PRODUCTIVE ENVIRONMENT!");
    }

    @Override
    public void setSettingsFile(@Nullable Path file) {
        // noop
    }

    @Override
    public @NotNull byte[] createPrivateKey() throws SimpleCryptException {
        return new byte[0];
    }

    @Override
    public byte[] createPrivateKeyFile(@Nullable Path settingsFile, @Nullable Path relocationFile, boolean force) throws SimpleCryptException {
        return new byte[0];
    }

    @Override
    public void importPrivateKey(@NotNull byte[] encodedPrivateKey, boolean force) throws SimpleCryptException {
        // noop
    }

    @Override
    public @Nullable String encrypt(byte[] decrypted) throws SimpleCryptException {
        return decrypted == null ? null : "{" + Base64.getEncoder().encodeToString(decrypted) + "}";
    }

    @Override
    public @Nullable String encrypt(char[] decrypted) throws SimpleCryptException {
        return decrypted == null ? null : "{" + Base64.getEncoder().encodeToString(UTF8.toByteArray(decrypted)) + "}";
    }

    @Override
    public @Nullable char[] decryptToChars(@Nullable String encryptedBase64) throws SimpleCryptException {
        return encryptedBase64 == null ? null : UTF8.toCharArray(Base64.getDecoder().decode(encryptedBase64.substring(1, encryptedBase64.length()-1)));
    }

}
