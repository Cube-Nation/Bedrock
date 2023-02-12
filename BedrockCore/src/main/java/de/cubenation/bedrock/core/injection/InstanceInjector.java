package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.database.Database;
import de.cubenation.bedrock.core.datastore.Datastore;
import de.cubenation.bedrock.core.exception.InjectionException;
import de.cubenation.bedrock.core.service.AbstractService;

import java.lang.reflect.Field;
import java.util.logging.Level;

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
        Class<?> fieldType = field.getType();
        InstanceSupplier<?> supplier = getInstanceSupplier(plugin, fieldType);
        if (supplier == null) {
            throw new InjectionException(String.format("Could create %s for %s; not a known class", InstanceSupplier.class.getName(), fieldType.getName()));
        }

        Object value = supplier.getInstance(field);
        if (value == null) {
            plugin.log(Level.WARNING, String.format("Injecting null value into %s in %s", field.getName(), target.getClass().getName()));
        }

        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new InjectionException("Could not inject value", e);
        }
    }

    private static InstanceSupplier<?> getInstanceSupplier(FoundationPlugin plugin, Class<?> fieldClass) {
        if (AbstractService.class.isAssignableFrom(fieldClass)) {
            return new ServiceSupplier(plugin);
        } else if (CustomConfigurationFile.class.isAssignableFrom(fieldClass)) {
            return new ConfigSupplier(plugin);
        } else if (Database.class.equals(fieldClass)) {
            return new DatabaseSupplier(plugin);
        } else if (Datastore.class.equals(fieldClass)) {
            return new DatastoreSupplier(plugin);
        }
        return null;
    }
}
