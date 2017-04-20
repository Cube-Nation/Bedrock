package de.cubenation.api.bedrock.service.config;

import de.cubenation.api.bedrock.BasePlugin;
import net.cubespace.Yamler.Config.YamlConfig;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class CustomConfigurationFile extends YamlConfig {

    public CustomConfigurationFile() { }

    @SuppressWarnings("unused")
    public CustomConfigurationFile(BasePlugin plugin, String name) { }

    public CustomConfigurationFile get() {
        return this;
    }

}