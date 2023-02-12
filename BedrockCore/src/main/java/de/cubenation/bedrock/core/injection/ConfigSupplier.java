package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.service.config.ConfigService;

import java.lang.reflect.Field;

public class ConfigSupplier extends InstanceSupplier<CustomConfigurationFile> {

    public ConfigSupplier(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public CustomConfigurationFile getInstance(Field field) {
        Class<?> configClass = field.getType();
        ConfigService service = (ConfigService) plugin.getServiceManager().getService(ConfigService.class);
        return service.getConfig(configClass);
    }
}
