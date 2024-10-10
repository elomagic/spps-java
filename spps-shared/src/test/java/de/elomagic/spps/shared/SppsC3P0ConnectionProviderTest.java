package de.elomagic.spps.shared;

import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JdbcSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class SppsC3P0ConnectionProviderTest {

    @Test
    void testConfigure() {
        SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        Map<String, Object> securedProperties = new HashMap<>();
        securedProperties.put(JdbcSettings.JAKARTA_JDBC_DRIVER, provider.encrypt("driverSecret".toCharArray()));
        securedProperties.put(JdbcSettings.JAKARTA_JDBC_URL, provider.encrypt("urlSecret".toCharArray()));
        securedProperties.put(JdbcSettings.JAKARTA_JDBC_USER, provider.encrypt("userSecret".toCharArray()));
        securedProperties.put(JdbcSettings.JAKARTA_JDBC_PASSWORD, provider.encrypt("passwordSecret".toCharArray()));

        SppsC3P0ConnectionProvider c3po = new SppsC3P0ConnectionProvider();
        c3po.injectServices(new ServiceRegistryImplementorMock());
        c3po.configure(securedProperties);

        // Implementation has made a copy of cfg?
        Assertions.assertTrue(provider.isEncryptedValue(securedProperties.get(Environment.JAKARTA_JDBC_DRIVER).toString()));

        Assertions.assertEquals("driverSecret", new String(provider.decryptToChars(securedProperties.get(JdbcSettings.JAKARTA_JDBC_DRIVER).toString())));
        Assertions.assertEquals("urlSecret", new String(provider.decryptToChars(securedProperties.get(JdbcSettings.JAKARTA_JDBC_URL).toString())));
        Assertions.assertEquals("userSecret", new String(provider.decryptToChars(securedProperties.get(JdbcSettings.JAKARTA_JDBC_USER).toString())));
        Assertions.assertEquals("passwordSecret", new String(provider.decryptToChars(securedProperties.get(JdbcSettings.JAKARTA_JDBC_PASSWORD).toString())));

        Map<String, Object> unsecuredProperties = new HashMap<>();
        unsecuredProperties.put(JdbcSettings.JAKARTA_JDBC_DRIVER, "driver");
        unsecuredProperties.put(JdbcSettings.JAKARTA_JDBC_URL, "url");
        unsecuredProperties.put(JdbcSettings.JAKARTA_JDBC_USER, "user");
        unsecuredProperties.put(JdbcSettings.JAKARTA_JDBC_PASSWORD, "password");

        c3po.configure(unsecuredProperties);

        Assertions.assertFalse(provider.isEncryptedValue(unsecuredProperties.get(JdbcSettings.JAKARTA_JDBC_DRIVER).toString()));

        Assertions.assertEquals("driver", unsecuredProperties.get(JdbcSettings.JAKARTA_JDBC_DRIVER));
        Assertions.assertEquals("url", unsecuredProperties.get(JdbcSettings.JAKARTA_JDBC_URL));
        Assertions.assertEquals("user", unsecuredProperties.get(JdbcSettings.JAKARTA_JDBC_USER));
        Assertions.assertEquals("password", unsecuredProperties.get(JdbcSettings.JAKARTA_JDBC_PASSWORD));

    }

}