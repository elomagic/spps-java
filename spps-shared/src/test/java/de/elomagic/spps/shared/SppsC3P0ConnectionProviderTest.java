package de.elomagic.spps.shared;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.Environment;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.mockito.Mockito.*;

class SppsC3P0ConnectionProviderTest {

    ClassLoaderService mockClassLoaderService() {
        ClassLoaderService classLoaderService = mock(ClassLoaderService.class, CALLS_REAL_METHODS);
        when(classLoaderService.classForName(anyString())).thenAnswer(cn -> Object.class);

        return classLoaderService;
    }

    ServiceRegistryImplementor mockRegistry() {
        ServiceRegistryImplementor serviceRegistryImplementor = mock(ServiceRegistryImplementor.class, CALLS_REAL_METHODS);
        when(serviceRegistryImplementor.getService(any())).thenAnswer(s -> mockClassLoaderService());

        return serviceRegistryImplementor;
    }

    @Test
    void testConfigure() {
        SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        Properties securedProperties = new Properties();
        securedProperties.setProperty(Environment.DRIVER, provider.encrypt("driverSecret".toCharArray()));
        securedProperties.setProperty(Environment.URL, provider.encrypt("urlSecret".toCharArray()));
        securedProperties.setProperty(Environment.USER, provider.encrypt("userSecret".toCharArray()));
        securedProperties.setProperty(Environment.PASS, provider.encrypt("passwordSecret".toCharArray()));

        SppsC3P0ConnectionProvider c3po = new SppsC3P0ConnectionProvider();
        c3po.injectServices(mockRegistry());
        c3po.configure(securedProperties);

        // Implementation has made a copy of cfg?
        Assertions.assertTrue(provider.isEncryptedValue(securedProperties.getProperty(Environment.DRIVER)));

        Assertions.assertEquals("driverSecret", new String(provider.decryptToChars(securedProperties.getProperty(Environment.DRIVER))));
        Assertions.assertEquals("urlSecret", new String(provider.decryptToChars(securedProperties.getProperty(Environment.URL))));
        Assertions.assertEquals("userSecret", new String(provider.decryptToChars(securedProperties.getProperty(Environment.USER))));
        Assertions.assertEquals("passwordSecret", new String(provider.decryptToChars(securedProperties.getProperty(Environment.PASS))));

        Properties unsecuredProperties = new Properties();
        unsecuredProperties.setProperty(Environment.DRIVER, "driver");
        unsecuredProperties.setProperty(Environment.URL, "url");
        unsecuredProperties.setProperty(Environment.USER, "user");
        unsecuredProperties.setProperty(Environment.PASS, "password");

        c3po.configure(unsecuredProperties);

        Assertions.assertFalse(provider.isEncryptedValue(unsecuredProperties.getProperty(Environment.DRIVER)));

        Assertions.assertEquals("driver", unsecuredProperties.getProperty(Environment.DRIVER));
        Assertions.assertEquals("url", unsecuredProperties.getProperty(Environment.URL));
        Assertions.assertEquals("user", unsecuredProperties.getProperty(Environment.USER));
        Assertions.assertEquals("password", unsecuredProperties.getProperty(Environment.PASS));

    }

}