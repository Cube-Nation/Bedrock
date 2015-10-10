package de.cubenation.bedrock;

import de.cubenation.bedrock.config.BedrockDefaults;
import de.cubenation.bedrock.config.locale.de_DE;
import de.cubenation.bedrock.config.locale.en_US;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.ebean.BedrockWorld;
import de.cubenation.bedrock.helper.version.VersionComparator;
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

    public void onPreEnable() throws Exception {
        setInstance(this);
        this.checkYamlerVersion();
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
    public HashMap<String, String> getCustomConfigurationFiles() {
        return new HashMap<String, String>() {{
            put(de_DE.getFilename(), de_DE.class.getName());
            put(en_US.getFilename(), en_US.class.getName());
        }};
    }

    @Override
    public void setCommands(HashMap<String, ArrayList<Class<?>>> commands) {

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

    private void checkYamlerVersion() throws Exception {
        String required_yamler_version = "2.3.1";

        VersionComparator cmp = new VersionComparator();
        String yamler_version = this.getPlugin("Yamler").getDescription().getVersion();
        if (yamler_version.matches(".+-.+"))
            yamler_version = yamler_version.split("-")[0];

        int result = cmp.compare(yamler_version, required_yamler_version);
        if (result < 0)
            throw new Exception(String.format(
                    "Bedrock dependency error: You need at least version %s of the Yamler-Bukkit plugin",
                    required_yamler_version
            ));
    }

}
