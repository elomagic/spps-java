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
import java.util.Arrays;

/**
 * Provides util method to handle UTF8 encoded chars and bytes.
 */
public final class UTF8 {

    private UTF8() {
    }

    /**
     * Convert a UTF8 encoded char array into a byte array.
     *
     * Input parameter will be wiped
     *
     * @param chars Char array
     * @return Returns a byte array
     */
    public static byte[] toByteArray(@Nullable final char[] chars) {
        if (chars == null) {
            return null;
        }

        // TODO Change to save type cast
        byte[] result = new String(chars).getBytes(StandardCharsets.UTF_8);

        Arrays.fill(chars, '*');

        return result;
    }

    /**
     * Convert a UTF8 encoded byte array into a char array.
     *
     * @param bytes Byte array
     * @return Returns a char array
     */
    public static char[] toCharArray(@Nullable final byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        // TODO Change to save type cast
        char[] chars = new String(bytes, StandardCharsets.UTF_8).toCharArray();

        Arrays.fill(bytes, (byte)0);

        return chars;
    }

}
