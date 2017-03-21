package de.cubenation.api.bedrock.service.config;

import de.cubenation.api.bedrock.BasePlugin;
import net.cubespace.Yamler.Config.YamlConfig;

public abstract class CustomConfigurationFile extends YamlConfig {

    public CustomConfigurationFile() { }

    @SuppressWarnings("unused")
    public CustomConfigurationFile(BasePlugin plugin, String name) { }

    public CustomConfigurationFile get() {
        return this;
    }

}