package de.elomagic.spps.shared;

import jakarta.annotation.Nonnull;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceRegistryImplementorMock implements ServiceRegistryImplementor {

    @Override
    public <R extends Service> ServiceBinding<R> locateServiceBinding(@Nonnull Class<R> aClass) {
        return null;
    }

    @Override
    public void destroy() {
        // noop
    }

    @Override
    public void registerChild(@Nonnull ServiceRegistryImplementor serviceRegistryImplementor) {
        // noop
    }

    @Override
    public void deRegisterChild(@Nonnull ServiceRegistryImplementor serviceRegistryImplementor) {
        // noop
    }

    @Override
    public <T extends Service> T fromRegistryOrChildren(@Nonnull Class<T> aClass) {
        return null;
    }

    @Override
    public ServiceRegistry getParentServiceRegistry() {
        return null;
    }


    ClassLoaderService mockClassLoaderService() {
        ClassLoaderService classLoaderService = mock(ClassLoaderService.class, CALLS_REAL_METHODS);
        when(classLoaderService.classForName(anyString())).thenAnswer(cn -> Object.class);

        return classLoaderService;
    }

    @Override
    public <R extends Service> R getService(@Nonnull Class<R> aClass) {
        return (R)mockClassLoaderService();
    }


}
