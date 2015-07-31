package de.cubenation.bedrock;

import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.config.BedrockDefaults;
import de.cubenation.bedrock.config.locale.de_DE;
import de.cubenation.bedrock.config.locale.en_US;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

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
            BedrockDefaults bd = new BedrockDefaults(this, null);
            bd.init();
        } catch (InvalidConfigurationException e) {
            this.log(Level.SEVERE, "Error creating config.yml", e);
            this.disable(e);
        }
    }

    @Override
    public HashMap<String, ArrayList<AbstractCommand>> getCommands() {
        return null;
    }

    @Override
    public HashMap<String, String> getCustomConfigurationFiles() {
        return new HashMap<String, String>() {{
            put("locale" + System.getProperty("file.separator") + "en_US.yml", en_US.class.getName());
            put("locale" + System.getProperty("file.separator") + "de_DE.yml", de_DE.class.getName());
        }};
    }

}
