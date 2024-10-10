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

import jakarta.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Provides util method to handle UTF8 encoded chars and bytes.
 * <p>
 * Copyright (c) 2000-2021 The Legion of the Bouncy Castle Inc. (<a href="https://www.bouncycastle.org">https://www.bouncycastle.org</a>)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 */
public final class UTF8 {

    // Constants for the categorization of code units
    private static final byte C_ILL = 0; //- C0..C1, F5..FF  ILLEGAL octets that should never appear in a UTF-8 sequence
    private static final byte C_CR1 = 1; //- 80..8F          Continuation range 1
    private static final byte C_CR2 = 2; //- 90..9F          Continuation range 2
    private static final byte C_CR3 = 3; //- A0..BF          Continuation range 3
    private static final byte C_L2A = 4; //- C2..DF          Leading byte range A / 2-byte sequence
    private static final byte C_L3A = 5; //- E0              Leading byte range A / 3-byte sequence
    private static final byte C_L3B = 6; //- E1..EC, EE..EF  Leading byte range B / 3-byte sequence
    private static final byte C_L3C = 7; //- ED              Leading byte range C / 3-byte sequence
    private static final byte C_L4A = 8; //- F0              Leading byte range A / 4-byte sequence
    private static final byte C_L4B = 9; //- F1..F3          Leading byte range B / 4-byte sequence
    private static final byte C_L4C = 10; //- F4             Leading byte range C / 4-byte sequence
    //  private static final byte C_ASC = 11; //- 00..7F         ASCII leading byte range

    // Constants for the states of a DFA
    private static final byte S_ERR = -2; //- Error state
    private static final byte S_END = -1; //- End (or Accept) state
    private static final byte S_CS1 = 0x00; //- Continuation state 1
    private static final byte S_CS2 = 0x10; //- Continuation state 2
    private static final byte S_CS3 = 0x20; //- Continuation state 3
    private static final byte S_P3A = 0x30; //- Partial 3-byte sequence state A
    private static final byte S_P3B = 0x40; //- Partial 3-byte sequence state B
    private static final byte S_P4A = 0x50; //- Partial 4-byte sequence state A
    private static final byte S_P4B = 0x60; //- Partial 4-byte sequence state B

    private static final short[] firstUnitTable = new short[128];
    private static final byte[] transitionTable = new byte[S_P4B + 16];

    private static void fill(byte[] table, int first, int last, byte b) {
        IntStream.range(first, last + 1).forEach(i -> table[i] = b);
    }

    static {
        byte[] categories = new byte[128];

        fill(categories, 0x00, 0x0F, C_CR1);
        fill(categories, 0x10, 0x1F, C_CR2);
        fill(categories, 0x20, 0x3F, C_CR3);
        fill(categories, 0x40, 0x41, C_ILL);
        fill(categories, 0x42, 0x5F, C_L2A);
        fill(categories, 0x60, 0x60, C_L3A);
        fill(categories, 0x61, 0x6C, C_L3B);
        fill(categories, 0x6D, 0x6D, C_L3C);
        fill(categories, 0x6E, 0x6F, C_L3B);
        fill(categories, 0x70, 0x70, C_L4A);
        fill(categories, 0x71, 0x73, C_L4B);
        fill(categories, 0x74, 0x74, C_L4C);
        fill(categories, 0x75, 0x7F, C_ILL);

        fill(transitionTable, 0, transitionTable.length - 1, S_ERR);
        fill(transitionTable, S_CS1 + 0x8, S_CS1 + 0xB, S_END);
        fill(transitionTable, S_CS2 + 0x8, S_CS2 + 0xB, S_CS1);
        fill(transitionTable, S_CS3 + 0x8, S_CS3 + 0xB, S_CS2);
        fill(transitionTable, S_P3A + 0xA, S_P3A + 0xB, S_CS1);
        fill(transitionTable, S_P3B + 0x8, S_P3B + 0x9, S_CS1);
        fill(transitionTable, S_P4A + 0x9, S_P4A + 0xB, S_CS2);
        fill(transitionTable, S_P4B + 0x8, S_P4B + 0x8, S_CS2);

        byte[] firstUnitMasks = { 0x00, 0x00, 0x00, 0x00, 0x1F, 0x0F, 0x0F, 0x0F, 0x07, 0x07, 0x07 };
        byte[] firstUnitTransitions = { S_ERR, S_ERR, S_ERR, S_ERR, S_CS1, S_P3A, S_CS2, S_P3B, S_P4A, S_CS3, S_P4B };

        for (int i = 0x00; i < 0x80; ++i) {
            byte category = categories[i];

            int codePoint = i & firstUnitMasks[category];
            byte state = firstUnitTransitions[category];

            firstUnitTable[i] = (short)((codePoint << 8) | state);
        }
    }

    private UTF8() {
    }

    /**
     * Transcode a UTF-16 (two-byte) encoded char array into a UTF-8 encoded byte array.
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
     * Transcode a UTF-8 encoding into a UTF-16 representation.
     *
     * Input parameter will be wiped.
     * <p>
     * Decoding of UTF-8 is based on a presentation by Bob Steagall at CppCon2018
     * (see https://github.com/BobSteagall/CppCon2018). It uses a Deterministic Finite Automaton (DFA) to recognize and
     * decode multi-byte code points.
     * <p>
     *
     * @param utf8 Nullable UTF-8 encoded byte array.
     * @return Returns a UTF-16 encoded char array or null
     */
    public static char[] toCharArray(@Nullable final byte[] utf8) throws SimpleCryptException {
        if (utf8 == null) {
            return null;
        }

        char[] result;
        try (SecureCharArrayWriter out = new SecureCharArrayWriter()) {

            int i = 0;

            while (i < utf8.length) {
                byte codeUnit = utf8[i++];
                if (codeUnit >= 0) {
                    out.write(codeUnit);
                    continue;
                }

                short first = firstUnitTable[codeUnit & 0x7F];
                int codePoint = first >>> 8;
                byte state = (byte) first;

                while (state >= 0) {
                    if (i >= utf8.length) {
                        throw new SimpleCryptException("Invalid UTF-8 encoded byte array input parameter.");
                    }

                    codeUnit = utf8[i++];
                    codePoint = (codePoint << 6) | (codeUnit & 0x3F);
                    state = transitionTable[state + ((codeUnit & 0xFF) >>> 4)];
                }

                if (state == S_ERR) {
                    throw new SimpleCryptException("Invalid UTF-8 encoded byte array input parameter.");
                }

                if (codePoint <= 0xFFFF) {
                    // Code points from U+D800 to U+DFFF are caught by the DFA
                    out.write(codePoint);
                } else {
                    // Code points above U+10FFFF are caught by the DFA
                    out.write(0xD7C0 + (codePoint >>> 10));
                    out.write(0xDC00 | (codePoint & 0x3FF));
                }
            }

            result = out.toCharArray();
        } finally {
            Arrays.fill(utf8, (byte) 0);
        }

        return result;
    }

}
