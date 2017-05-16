/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.plugin.bedrock;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.DatabasePlugin;
import de.cubenation.api.bedrock.annotation.CommandHandler;
import de.cubenation.api.bedrock.annotation.ConfigurationFile;
import de.cubenation.api.bedrock.config.BedrockDefaults;
import de.cubenation.api.bedrock.config.locale.de_DE;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.ebean.BedrockWorld;
import de.cubenation.plugin.bedrock.command.BedrockPlayerInfoCommand;
import de.cubenation.plugin.bedrock.listener.EbeanListener;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This is the Bedrock main plugin class
 *
 * @author Cube-Nation
 * @version 1.0
 */
@ConfigurationFile(de_DE.class)
@CommandHandler(Command = "bp", Handlers = {
        BedrockPlayerInfoCommand.class
})
public class BedrockPlugin extends BasePlugin {

    private static BedrockPlugin instance;

    public static BedrockPlugin getInstance() {
        return instance;
    }

    public void onPreEnable() throws Exception {
        instance = this;
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

    /**
     * Returns the database class that handles player mapping
     *
     * @return A list of database classes.
     */
    @Override
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>() {{
            add(BedrockPlayer.class);
            add(BedrockWorld.class);
        }};
    }

}
