package de.cubenation.bedrock.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;

import java.io.IOException;
import java.util.HashMap;

public class Permissions extends CustomConfigurationFile {

    public Permissions(BasePlugin plugin) throws IOException {
        super(
                plugin,
                plugin.getPluginConfigService().getConfig().getString(
                        "service.permission.file_name",
                        BedrockPlugin.getInstance().getPluginConfigService().getConfig().getString(
                                "service.permission.file_name",
                                "permissions.yml"
                        )
                ),
                Permissions.data()
        );
    }

    private static HashMap<String,Object> data() {
        return new HashMap<>();
    }

}
