package de.cubenation.bedrock.core.service.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import net.cubespace.Yamler.Config.YamlConfig;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class CustomConfigurationFile extends YamlConfig {

    public CustomConfigurationFile() { }

    @SuppressWarnings("unused")
    public CustomConfigurationFile(FoundationPlugin plugin, String name) { }

    public CustomConfigurationFile get() {
        return this;
    }

}