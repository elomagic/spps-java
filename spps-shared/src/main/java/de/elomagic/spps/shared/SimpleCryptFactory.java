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

import javax.enterprise.inject.Produces;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This tooling class can be used to get the default/current instance class.
 */
public final class SimpleCryptFactory {

    private static final String META_INF_PATH = "/META-INF/de.elomagic.spps/provider";
    private static final Logger LOGGER = LogManager.getLogger(SimpleCryptFactory.class);
    private static final AtomicReference<SimpleCryptProvider> ACTIVE_PROVIDER_INSTANCE = new AtomicReference<>();

    private SimpleCryptFactory() {
    }

    @NotNull
    private static String readProviderClassNameFromMataInf() {
         InputStream in = SimpleCryptFactory.class.getResourceAsStream(META_INF_PATH);
        if (in == null) {
            throw new SimpleCryptException("No SPPS provider file '" + META_INF_PATH + "' found.");
        } else {
            String className;
            try {
                className = new BufferedReader(new InputStreamReader(in)).readLine().trim();
            } catch (IOException ex) {
                throw new SimpleCryptException("Unable to read class name from SPPS provider file '" + META_INF_PATH + "'.");
            }

            if (className.isEmpty()) {
                throw new SimpleCryptException("Provider class name not set in file '" + META_INF_PATH + "'.");
            }

            return className;
        }
    }

    /**
     * Returns current provider instance.
     * <p/>
     * This method will also be used by CDI when inject instance.
     *
     * @return The current provider
     */
    @Produces
    @NotNull
    public static SimpleCryptProvider getInstance() {
        if (ACTIVE_PROVIDER_INSTANCE.get() == null) {
            String className = readProviderClassNameFromMataInf();
            try {
                LOGGER.debug("Creating default instance of SPPS provider '{}'.", className);
                Class<? extends SimpleCryptProvider> providerClass = (Class<? extends SimpleCryptProvider>)Class.forName(className);
                setProvider(providerClass);
            } catch (Exception ex) {
                throw new SimpleCryptException("Unable to get active instance of provider class '" + className + "': " + ex.getMessage(), ex);
            }
        }

        return ACTIVE_PROVIDER_INSTANCE.get();
    }

    /**
     * Set the current SPPS provider.
     *
     * @param provider Provider
     * @see SimpleCryptFactory#getInstance()
     */
    public static void setProvider(@Nullable final SimpleCryptProvider provider) {
        ACTIVE_PROVIDER_INSTANCE.set(provider);
    }

    /**
     * Set the current SPPS provider class.
     *
     * This class will be created a new current provider instance.
     *
     * @param providerClass Class type of the provider
     * @throws SimpleCryptException Thrown when unable to create new instance of given provider class
     * @see SimpleCryptFactory#getInstance()
     * @see SimpleCryptFactory#setProvider(SimpleCryptProvider)
     */
    public static void setProvider(@NotNull final Class<? extends SimpleCryptProvider> providerClass) throws SimpleCryptException {
        try {
            SimpleCryptProvider instance = providerClass.getConstructor().newInstance();

            ACTIVE_PROVIDER_INSTANCE.set(instance);
        } catch (Exception ex) {
            throw new SimpleCryptException("Unable to create provider class '" + providerClass.getName() + "': " + ex.getMessage(), ex);
        }
    }

}
