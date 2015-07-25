package de.cubenation.bedrock.service.localization;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;

public class DefaultLocale extends CustomConfigurationFile {

    public DefaultLocale(JavaPlugin plugin) throws IOException {
        super(
                plugin,
                    "locale" +
                    java.lang.System.getProperty("file.separator") +
                    BedrockPlugin.getInstance().getConfig().getString("service.localization.locale") +
                    ".yml",
                DefaultLocale.data()
        );
    }

    public static HashMap<String,Object> data() {
        return new HashMap<String,Object>();
    }

}
