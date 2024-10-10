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

import jakarta.annotation.Nonnull;

import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.JdbcSettings;

import java.util.HashMap;
import java.util.Map;

public final class SppsC3P0ConnectionProvider extends C3P0ConnectionProvider {

    private void decryptIfRequired(@Nonnull final Map<String, Object> cfg, final String property) {
        if (!cfg.containsKey(property)) {
            return;
        }

        String value = cfg.get(property).toString();

        final SimpleCryptProvider provider = SimpleCryptFactory.getInstance();
        if (provider.isEncryptedValue(value)) {
            cfg.put(property, String.valueOf(provider.decryptToChars(value)));
        }
    }

    /**
     * Configure the service.
     *
     * @param cfg The configuration properties.
     */
    @Override
    public void configure(@Nonnull final Map<String, Object> cfg) {

        // Make a copy. Don't work with the original properties class!
        Map<String, Object> props = new HashMap<>(cfg);

        decryptIfRequired(props, JdbcSettings.JAKARTA_JDBC_DRIVER);
        decryptIfRequired(props, JdbcSettings.JAKARTA_JDBC_URL);
        decryptIfRequired(props, JdbcSettings.JAKARTA_JDBC_USER);
        decryptIfRequired(props, JdbcSettings.JAKARTA_JDBC_PASSWORD);

        super.configure(props);

    }

}
