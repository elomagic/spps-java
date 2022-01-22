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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public final class SecureProperties implements Closeable {

    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final byte[] CRLF = new byte[] { CR, LF };

    private final Map<String, byte[]> data = new HashMap<>();

    public void wipe() {
        data.values().forEach(v -> Arrays.fill(v, (byte)0));
        data.clear();
    }

    @Override
    public void close() {
        wipe();
    }

    public void read(final Path file) throws IOException {
        byte[] readData = Files.readAllBytes(file);
        try {
            read(readData);
        } finally {
            Arrays.fill(readData, (byte)0);
        }
    }

    private int indexOf(final byte[] data, final byte b, final int from) {
        return IntStream.range(from, data.length)
                .filter(i -> data[i] == b)
                .findFirst()
                .orElse(-1);
    }

    private void read(final byte[] readData) {
        if (readData == null || readData.length == 0) {
            return;
        }

        byte[] dc = readData.clone();
        try {
            // Normalize line breaks
            IntStream.range(0, dc.length)
                    .filter(i -> dc[i] == LF)
                    .forEach(i -> dc[i] = CR);

            // Iterate over each line
            int index = 0;
            int lastIndex = 0;
            while ((index = indexOf(dc, CR, lastIndex)) >= lastIndex) {
                byte[] line = Arrays.copyOfRange(dc, lastIndex, index);
                try {
                    readLine(line);
                    lastIndex = index + 1;
                } finally {
                    Arrays.fill(line, (byte)0);
                }
            }
        } finally {
            Arrays.fill(dc, (byte)0);
        }
    }

    private void readLine(final byte[] line) {
        if (line.length < 2 || line[0] == '#') {
            return;
        }

        int index = indexOf(line, (byte)'=', 1);

        if (index < 2) {
            return;
        }

        String key = new String(Arrays.copyOfRange(line, 0, index), StandardCharsets.UTF_8);
        byte[] value = Arrays.copyOfRange(line, index + 1, line.length);

        setKey(key, value);
    }

    public int size() {
        return data.size();
    }

    /**
     * Checks that key exists and value is not empty.
     *
     * @param key Key to check
     * @return Returns true when key exists and value is not empty
     */
    public boolean containsValue(@NotNull final String key) {
        return data.getOrDefault(key, new byte[0]).length != 0;
    }

    public byte[] getValueAsBytes(@NotNull final String key) {
        return data.get(key);
    }

    public void setKey(@NotNull final String key, final byte[] value) {
        if (containsValue(key)) {
            Arrays.fill(getValueAsBytes(key), (byte)0);
        }

        data.put(key, value == null ? new byte[0]:value);
    }

    public void write(final Path file) {
        try (OutputStream out = Files.newOutputStream(file)) {
            // Write UTF-8 header
            // TODO FixIt out.write('\ufeef'); // emits 0xef
            //out.write('\ufebb'); // emits 0xbb
            //out.write('\ufebf'); // emits 0xbf

            out.write("# SPPS Settings - Created by spps-java".getBytes(StandardCharsets.UTF_8));
            out.write(CRLF);

            data.forEach((key, value) -> writeKey(out, key, value));

            out.flush();
        } catch (Exception ex) {
            throw new SimpleCryptException("Unable to write settings file: " + ex.getMessage(), ex);
        }
    }

    private void writeKey(@NotNull final OutputStream out, @NotNull final String key, final byte[] value) {
        try {
            out.write(key.getBytes(StandardCharsets.UTF_8));
            out.write('=');

            if (value != null && value.length > 0) {
                out.write(value);
            }

            out.write(CRLF);
        } catch (IOException ex) {
            throw new SimpleCryptException("Unable to write key '" + key + "': " + ex.getMessage(), ex);
        }
    }


}
