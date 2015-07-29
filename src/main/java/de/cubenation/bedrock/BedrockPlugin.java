package de.cubenation.bedrock;

import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.config.locale.de_DE;
import de.cubenation.bedrock.config.locale.en_US;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public class BedrockPlugin extends BasePlugin {

    private static BedrockPlugin instance;

    public static void setInstance(BedrockPlugin plugin) {
        instance = plugin;
    }

    public static BedrockPlugin getInstance() {
        return instance;
    }

    public void onPreEnable() {
        setInstance(this);
    }

    @Override
    public ArrayList<CommandManager> getCommandManager() {
        return null;
    }

    @Override
    public ArrayList<CustomConfigurationFile> getCustomConfigurationFiles() throws IOException {
        return new ArrayList<CustomConfigurationFile>() {{
            add(new de_DE(BedrockPlugin.getInstance()));
            add(new en_US(BedrockPlugin.getInstance()));
        }};
    }

}
