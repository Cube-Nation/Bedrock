package de.cubenation.plugin.bedrock.config;

import de.cubenation.api.bedrock.BasePlugin;

import java.io.File;

/**
 * Created by bhruschka on 20.04.17.
 * Project: Bedrock
 */
public class BedrockDefaults extends de.cubenation.api.bedrock.config.BedrockDefaults {

    public BedrockDefaults(BasePlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), de.cubenation.plugin.bedrock.config.BedrockDefaults.getFilename());
        CONFIG_HEADER = getHeader();

        this.setColorSchemeName("RED");
        this.setLocalizationLocale("de_DE");
    }

}