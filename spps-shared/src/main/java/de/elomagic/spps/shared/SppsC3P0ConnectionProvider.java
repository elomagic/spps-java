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

import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public final class SppsC3P0ConnectionProvider extends C3P0ConnectionProvider {

    /**
     * Configure the service.
     *
     * @param cfg The configuration properties.
     */
    public void configure(@NotNull final Properties cfg) {

        Properties props = new Properties(cfg);

        final SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        final String driver = props.getProperty(Environment.DRIVER);
        if (provider.isEncryptedValue(driver)) {
            props.setProperty(Environment.DRIVER, new String(provider.decryptToChars(driver)));
        }

        final String url = props.getProperty(Environment.URL);
        if (provider.isEncryptedValue(url)) {
            props.setProperty(Environment.URL, new String(provider.decryptToChars(url)));
        }

        final String user = props.getProperty(Environment.USER);
        if (provider.isEncryptedValue(user)) {
            props.setProperty(Environment.USER, new String(provider.decryptToChars(user)));
        }

        final String password = props.getProperty(Environment.PASS);
        if (provider.isEncryptedValue(password)) {
            props.setProperty(Environment.PASS, new String(provider.decryptToChars(password)));
        }

        super.configure(props);
    }

}
