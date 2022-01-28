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

import org.jetbrains.annotations.NotNull;

import java.io.Writer;
import java.util.Arrays;
import java.util.stream.IntStream;

public final class SecureCharArrayWriter extends Writer {

    private char[] buffer = new char[0];
    private int index = 0;

    /**
     * Cast an integer to a char and append it to the internal char array.
     *
     * @param b Byte to append
     */
    @Override
    public void write(int b) {
        char[] a = new char[] { (char)b };
        try {
            write(a, 0, 1);
        } finally {
            b = 0;
            wipe(a);
        }
    }

    /**
     * Append a char array to the internal char array.
     *
     * @param cbuf Array to append
     * @param off Offset to append
     * @param len Length to append
     */
    @Override
    public void write(@NotNull final char[] cbuf, int off, int len) {
        ensureCapacity(index + len);

        IntStream.range(index, index + len).forEach(i -> buffer[i] = cbuf[off + i - index]);
        index += len;
    }

    /**
     * Do nothing.
     */
    @Override
    public void flush() {
        // noop
    }

    /**
     * Wipes the internal char array.
     */
    @Override
    public void close() {
        wipe();
    }

    private void ensureCapacity(int minSize) {
        if (buffer.length > minSize) {
            return;
        }

        char[] newBuffer = new char[minSize];

        System.arraycopy(buffer, 0, newBuffer, 0, index);

        wipe(buffer);

        buffer = newBuffer;
    }

    /**
     * Wipes the internal char array.
     */
    public void wipe() {
        wipe(buffer);
    }

    private void wipe(@NotNull final char[] chars) {
        Arrays.fill(chars, '*');
    }

    /**
     * Returns a copy of the internal array buffer.
     *
     * @return A char array but never null
     */
    @NotNull
    public char[] toCharArray() {
        return Arrays.copyOf(buffer, index);
    }
}
