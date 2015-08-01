package de.cubenation.bedrock.service.config;

import de.cubenation.bedrock.BasePlugin;
import net.cubespace.Yamler.Config.Config;

public abstract class CustomConfigurationFile extends Config {

    public CustomConfigurationFile() { }

    public CustomConfigurationFile(BasePlugin plugin, String name) { }

    public CustomConfigurationFile get() {
        return this;
    }

}
