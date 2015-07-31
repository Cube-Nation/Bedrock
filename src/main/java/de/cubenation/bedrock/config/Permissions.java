package de.cubenation.bedrock.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;

import java.io.File;
import java.io.IOException;

public class Permissions extends CustomConfigurationFile {

    public Permissions(BasePlugin plugin) throws IOException {
        this.setFilename(plugin);
        CONFIG_FILE = new File(plugin.getDataFolder(), this.getFilename());
    }

    @Override
    public void setFilename(BasePlugin plugin) {
        this.filename = plugin.getConfigService().getConfig().getString(
                "service.permission.file_name",
                BedrockPlugin.getInstance().getConfigService().getConfig().getString(
                        "service.permission.file_name",
                        "permissions.yml"
                )
        );
    }

}
