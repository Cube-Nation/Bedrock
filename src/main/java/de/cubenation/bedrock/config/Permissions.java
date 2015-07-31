package de.cubenation.bedrock.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;

import java.io.File;
import java.io.IOException;

public class Permissions extends CustomConfigurationFile {

    public Permissions(BasePlugin plugin, String name) throws IOException {
        CONFIG_FILE = new File(plugin.getDataFolder(), name);
    }

}
