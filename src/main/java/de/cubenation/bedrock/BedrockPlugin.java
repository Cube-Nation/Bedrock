package de.cubenation.bedrock;

import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.config.BedrockDefaults;
import de.cubenation.bedrock.config.locale.de_DE;
import de.cubenation.bedrock.config.locale.en_US;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;

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

    public void onPostEnable() {
        try {
            BedrockDefaults bd = new BedrockDefaults(this);
            bd.init();
        } catch(InvalidConfigurationException ex) {
            System.out.println("Your Config YML was wrong");
            ex.printStackTrace();
        }
    }

    @Override
    public HashMap<String, ArrayList<AbstractCommand>> getCommands() {
        return null;
    }

    @Override
    public ArrayList<CustomConfigurationFile> getCustomConfigurationFiles() {
        return new ArrayList<CustomConfigurationFile>() {{
            add(new en_US(BedrockPlugin.getInstance()));
            add(new de_DE(BedrockPlugin.getInstance()));
        }};
    }

}
