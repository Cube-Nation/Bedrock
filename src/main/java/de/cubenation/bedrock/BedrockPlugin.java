package de.cubenation.bedrock;

import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.config.BedrockDefaults;
import de.cubenation.bedrock.config.locale.de_DE;
import de.cubenation.bedrock.config.locale.en_US;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.ebean.BedrockWorld;
import de.cubenation.bedrock.listener.EbeanListener;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        // create default configuration
        try {
            BedrockDefaults bd = new BedrockDefaults(this, null);
            bd.init();
        } catch (InvalidConfigurationException e) {
            this.log(Level.SEVERE, "Error creating config.yml", e);
            this.disable(e);
        }

        // install database table
        try {
            this.getDatabase().find(BedrockPlayer.class).findRowCount();
            this.getDatabase().find(BedrockWorld.class).findRowCount();
        } catch (PersistenceException e) {
            getLogger().log(Level.INFO, "Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }

        // register events for creation of
        //  - a BedrockPlayer - fired when a player joins the server
        //  - a Bedrockorld - fired when a world is loaded
        this.getServer().getPluginManager().registerEvents(new EbeanListener(), this);
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

    /**
     * Returns the database class that handles player mapping
     *
     * @return List<Class<?>> database classes
     */
    @Override
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>() {{
            add(BedrockPlayer.class);
            add(BedrockWorld.class);
        }};
    }

}
