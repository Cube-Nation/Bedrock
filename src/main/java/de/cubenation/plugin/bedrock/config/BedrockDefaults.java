package de.cubenation.plugin.bedrock.config;

import de.cubenation.api.bedrock.BasePlugin;

import java.io.File;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BedrockDefaults extends de.cubenation.api.bedrock.config.BedrockDefaults {

    public BedrockDefaults(BasePlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), de.cubenation.plugin.bedrock.config.BedrockDefaults.getFilename());
        CONFIG_HEADER = getHeader();

        this.setColorSchemeName("RED");
        this.setLocalizationLocale("de_DE");
    }

}