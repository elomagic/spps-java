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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Provides util method to handle UTF8 encoded chars and bytes.
 */
public final class UTF8 {

    private UTF8() {
    }

    /**
     * Convert a UTF-16 (two-byte) encoded char array into a UTF-8 encoded byte array.
     *
     * Input parameter will be wiped
     *
     * @param chars Char array. Supports also null arrays
     * @return Returns a byte array
     */
    public static byte[] toByteArray(@Nullable final char[] chars) {
        if (chars == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(chars.length * 2);

        int i = 0;
        while (i < chars.length) {
            char ch = chars[i];

            if (ch < 0x0080) {
                // ASCII char
                out.write(ch);
            } else if (ch < 0x0800) {
                // 2 byte char
                out.write(0xc0 | (ch >> 6));
                out.write(0x80 | (ch & 0x3f));
            } else if (ch >= 0xD800 && ch <= 0xDFFF) {
                // 4 byte char
                if (i + 1 >= chars.length) {
                    throw new IllegalStateException("Invalid UTF-16 codepoint");
                }

                char w1 = ch;
                ch = chars[++i];
                char w2 = ch;

                if (w1 > 0xDBFF) {
                    throw new IllegalStateException("Invalid UTF-16 codepoint");
                }

                int codePoint = (((w1 & 0x03FF) << 10) | (w2 & 0x03FF)) + 0x10000;
                out.write(0xf0 | (codePoint >> 18));
                out.write(0x80 | ((codePoint >> 12) & 0x3F));
                out.write(0x80 | ((codePoint >> 6) & 0x3F));
                out.write(0x80 | (codePoint & 0x3F));
            } else {
                // 3 byte char
                out.write(0xe0 | (ch >> 12));
                out.write(0x80 | ((ch >> 6) & 0x3F));
                out.write(0x80 | (ch & 0x3F));
            }

            i++;
        }

        Arrays.fill(chars, '*');

        return out.toByteArray();
    }

    /**
     * Convert a UTF-8 encoded byte array into a UTF-16 encoded char array.
     *
     * Input parameter will be wiped
     *
     * @param bytes Byte array. Supports also null arrays
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
