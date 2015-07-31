package de.cubenation.bedrock.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Permissions extends CustomConfigurationFile {

    public Permissions(BasePlugin plugin, String name) throws IOException {
        CONFIG_FILE = new File(plugin.getDataFolder(), name);
    }

    @Path("permissions")
    private HashMap<String,Object>  permissions = null;

}
