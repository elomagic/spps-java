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

import java.nio.charset.StandardCharsets;

/**
 * Provides util method to handle UTF8 encoded chars and bytes.
 */
public final class UTF8 {

    private UTF8() {
    }

    public static byte[] toByteArray(@Nullable final char[] chars) {
        // TODO Change to save type cast
        return chars == null ? null : new String(chars).getBytes(StandardCharsets.UTF_8);
    }

    public static char[] toCharArray(@Nullable final byte[] bytes) {
        // TODO Change to save type cast
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8).toCharArray();
    }

}
