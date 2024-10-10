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
import org.hibernate.cfg.AvailableSettings;

import java.util.Properties;

public final class SppsC3P0ConnectionProvider extends C3P0ConnectionProvider {

    /**
     * Configure the service.
     *
     * @param cfg The configuration properties.
     */
    public void configure(@Nonnull final Properties cfg) {

        // Make a copy. Don't work with the original properties class!
        Properties props = new Properties(cfg);

        final SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        final String driver = props.getProperty(AvailableSettings.DRIVER);
        if (provider.isEncryptedValue(driver)) {
            props.setProperty(AvailableSettings.DRIVER, String.valueOf(provider.decryptToChars(driver)));
        }

        final String url = props.getProperty(AvailableSettings.URL);
        if (provider.isEncryptedValue(url)) {
            props.setProperty(AvailableSettings.URL, String.valueOf(provider.decryptToChars(url)));
        }

        final String user = props.getProperty(AvailableSettings.USER);
        if (provider.isEncryptedValue(user)) {
            props.setProperty(AvailableSettings.USER, String.valueOf(provider.decryptToChars(user)));
        }

        final String password = props.getProperty(AvailableSettings.PASS);
        if (provider.isEncryptedValue(password)) {
            props.setProperty(AvailableSettings.PASS, String.valueOf(provider.decryptToChars(password)));
        }

        super.configure(props);

    }

}
