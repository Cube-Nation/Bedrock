package de.cubenation.api.bedrock;

import de.cubenation.api.bedrock.config.BedrockDefaults;
import de.cubenation.api.bedrock.command.player.BedrockPlayerInfoCommand;
import de.cubenation.api.bedrock.config.locale.de_DE;
import de.cubenation.api.bedrock.config.locale.en_US;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.ebean.BedrockWorld;
import de.cubenation.api.bedrock.listener.EbeanListener;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 */
public class BedrockPlugin extends BasePlugin {

    private static BedrockPlugin instance;

    public static void setInstance(BedrockPlugin plugin) {
        instance = plugin;
    }

    public static BedrockPlugin getInstance() {
        return instance;
    }

    public void onPreEnable() throws Exception {
        setInstance(this);
        this.assertPluginDependency("Yamler", "2.3.1");
    }

    public void onPostEnable() {
        // create default configuration
        try {
            BedrockDefaults bd = new BedrockDefaults(this);
            bd.init();
        } catch (InvalidConfigurationException e) {
            this.log(Level.SEVERE, "Error creating " + BedrockDefaults.getFilename(), e);
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
    public ArrayList<Class<?>> getCustomConfigurationFiles() {
        return new ArrayList<Class<?>>() {{
            add(de_DE.class);
            add(en_US.class);
        }};
    }

    @Override
    public void setCommands(HashMap<String, ArrayList<Class<?>>> commands) {
        commands.put("bp", new ArrayList<Class<?>>() {{
            add(BedrockPlayerInfoCommand.class);
        }});
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
