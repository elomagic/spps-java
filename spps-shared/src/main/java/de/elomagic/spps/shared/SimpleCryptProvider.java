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

public interface SimpleCryptProvider {

    boolean isEncryptedValue(@Nullable final String value);

    void createPrivateKeyFile(@Nullable final Path settingsFile, @Nullable final Path relocationFile, boolean force) throws SimpleCryptException;

    @Nullable
    String encrypt(final byte[] decrypted) throws SimpleCryptException;

    @Nullable
    String encrypt(final char[] decrypted) throws SimpleCryptException;

    @Nullable
    char[] decryptToChars(@Nullable final String encryptedBase64) throws SimpleCryptException;

}
