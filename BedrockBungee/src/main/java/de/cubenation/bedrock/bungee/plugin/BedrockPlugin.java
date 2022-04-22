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

package de.cubenation.bedrock.bungee.plugin;

import de.cubenation.bedrock.bungee.api.BasePlugin;
import de.cubenation.bedrock.bungee.plugin.io.IOVerbs;
import de.cubenation.bedrock.bungee.plugin.listener.PlayerListener;
import de.cubenation.bedrock.core.annotation.ConfigurationFile;
import de.cubenation.bedrock.core.config.locale.de_DE;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@ConfigurationFile(de_DE.class)
public class BedrockPlugin extends BasePlugin {

    public static BedrockPlugin instance;

    public static BedrockPlugin getInstance() {
        return instance;
    }

    @Override
    public void onPreEnable() throws Exception {
        instance = this;
        getLogger().info("Yay! It loads!");
    }

    @Override
    public void onPostEnable() throws Exception {
        // install database table
        try {
            this.getDatabase().find(BedrockOfflinePlayer.class).findRowCount();
        } catch (PersistenceException e) {
            getLogger().log(Level.INFO, "Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }

        getProxy().registerChannel(IOVerbs.CHANNEL);

        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>() {{
//            add(BedrockOfflinePlayer.class);
        }};
    }

}
