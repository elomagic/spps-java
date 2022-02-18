/*
 * Simple Password Protection Solution WebTool
 *
 * Copyright © 2021-present Carsten Rambow (spps.dev@elomagic.de)
 *
 * This file is part of Simple Password Protection Solution WebTool.
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
package de.elomagic.spps.wet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public enum GenerateMode {

    GENERATE_AND_IMPORT("generate-and-import"),
    GENERATE_IMPORT_AND_PRINT("generate-import-and-print"),
    GENERATE_AND_PRINT("print-only");

    private final String value;

    GenerateMode(String value) {
        this.value = value;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    @NotNull
    public static Optional<GenerateMode> parseString(@Nullable String text) {
        return Arrays
                .stream(values())
                .filter(i -> i.value.equals(text))
                .findFirst();
    }

}
