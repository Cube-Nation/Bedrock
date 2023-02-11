package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.exception.InjectionException;
import de.cubenation.bedrock.core.service.AbstractService;

import java.lang.reflect.Field;

public class InstanceInjector {

    public static void performInjection(FoundationPlugin plugin, Object target) throws InjectionException {
        for (Field field : target.getClass().getDeclaredFields()){
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            performInjection(plugin, target, field);
        }
    }

    private static void performInjection(FoundationPlugin plugin, Object target, Field field) throws InjectionException {
        Class<?> declaringClass = field.getDeclaringClass();
        InstanceSupplier<?> supplier = getInstanceSupplier(plugin, declaringClass);
        if (supplier == null) {
            throw new InjectionException(String.format("Could create %s for %s; not a known class", InstanceSupplier.class.getName(), declaringClass.getName()));
        }

        Object value = supplier.getInstance(field);

        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new InjectionException("Could not inject value", e);
        }
    }

    private static InstanceSupplier<?> getInstanceSupplier(FoundationPlugin plugin, Class<?> declaringClass) {
        if (AbstractService.class.isAssignableFrom(declaringClass)) {
            return new ServiceSupplier(plugin);
        } else if (CustomConfigurationFile.class.isAssignableFrom(declaringClass)) {
            return new ConfigSupplier(plugin);
        }
        return null;
    }
}
