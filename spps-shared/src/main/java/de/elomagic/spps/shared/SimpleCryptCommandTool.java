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
import org.apache.logging.log4j.core.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Class to be used as a simple command line tool.
 */
public final class SimpleCryptCommandTool {

    private static final Logger LOGGER = LogManager.getLogger(SimpleCryptCommandTool.class);

    private static final String ARG_FILE = "-File";
    private static final String ARG_FORCE = "-Force";
    private static final String ARG_SECRET = "-Secret";

    SimpleCryptCommandTool() {
    }

    boolean hasArgumentForOption(@NotNull final List<String> args, @NotNull final String option) {
        int index = args.indexOf(option);

        return (index != -1 && args.size() > index+1);
    }

    String getArgument(@NotNull final List<String> args, @NotNull final String option) {
        int index = args.indexOf(option);

        if (index == -1 || args.size() <= index+1) {
            throw new IllegalArgumentException("Syntax error. Argument not found.");
        }

        return args.get(index+1);
    }

    private char[] enterSecret() {
        return System.console().readPassword("Enter secret: ");
    }

    private PrintWriter out() {
        // For JUnit test we have to use System.out because console() will return null
        return System.console() == null ? new PrintWriter(System.out, true) : System.console().writer();
    }

    int run(@Nullable final String[] args) {
        try {
            List<String> argList = args == null ? Collections.emptyList() : Arrays.asList(args);

            SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

            if (argList.contains(ARG_SECRET)) {
                char[] secret;
                if (hasArgumentForOption(argList, ARG_SECRET)) {
                    secret = getArgument(argList, ARG_SECRET).toCharArray();
                } else {
                    secret = enterSecret();
                }
                out().println(provider.encrypt(secret));
            } else if (argList.contains("-CreatePrivateKey")) {
                boolean force = argList.contains(ARG_FORCE);
                Path relocationFile = argList.contains("-Relocation") ? Paths.get(getArgument(argList, "-Relocation")) : null;
                Path file = argList.contains(ARG_FILE) ? Paths.get(getArgument(argList, ARG_FILE)) : null;
                byte[] privateKey = provider.createPrivateKeyFile(file, relocationFile, force);

                if (argList.contains("-Print")) {
                    out().println(Base64.getEncoder().encodeToString(privateKey));
                }

                Arrays.fill(privateKey, (byte)0);
            } else if (argList.contains("-ImportPrivateKey")) {
                boolean force = argList.contains(ARG_FORCE);
                byte[] privateKey;
                if (hasArgumentForOption(argList, ARG_FILE)) {
                    Path file = Paths.get(getArgument(argList, ARG_FILE));
                    privateKey = Files.readAllBytes(file);
                } else {
                    // TODO Change to secure type cast
                    privateKey = new String(enterSecret()).getBytes(StandardCharsets.UTF_8);
                }

                provider.importPrivateKey(privateKey, force);
            } else {
                String resource = "/" + SimpleCryptCommandTool.class.getPackage().getName().replace(".", "/") + "/Help.txt";
                try (InputStream in = SimpleCryptCommandTool.class.getResourceAsStream(resource); InputStreamReader reader = new InputStreamReader(in)) {
                    String text = IOUtils.toString(reader);
                    out().println(text);
                }
            }
            return 0;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return 1;
        }
    }

    /**
     * Tooling method for simple and fast encrypting secrets.
     *
     * @param args First argument must contain value to encrypt
     */
    public static void main(@Nullable final String[] args) {
        int exitCode = new SimpleCryptCommandTool().run(args);
        System.exit(exitCode);
    }

}
