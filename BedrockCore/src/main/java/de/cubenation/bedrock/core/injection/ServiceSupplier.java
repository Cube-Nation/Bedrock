package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.AbstractService;

import java.lang.reflect.Field;

public class ServiceSupplier extends InstanceSupplier<AbstractService> {

    public ServiceSupplier(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public AbstractService getInstance(Field field) {
        Class<? extends AbstractService> fieldType = (Class<? extends AbstractService>) field.getType();
        return plugin.getServiceManager().getService(fieldType);
    }
}
