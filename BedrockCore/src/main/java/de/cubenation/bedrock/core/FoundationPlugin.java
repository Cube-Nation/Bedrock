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

package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.config.BedrockDefaultsConfig;
import de.cubenation.bedrock.core.injection.InstanceInjector;
import de.cubenation.bedrock.core.message.Messages;
import de.cubenation.bedrock.core.model.BedrockServer;
import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.command.ArgumentTypeService;
import de.cubenation.bedrock.core.service.command.CommandService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import de.cubenation.bedrock.core.service.database.DatabaseService;
import de.cubenation.bedrock.core.service.datastore.DatastoreService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.service.settings.SettingsService;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public interface FoundationPlugin {

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    void onPreEnable() throws Exception;

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    void onPostEnable() throws Exception;

    /**
     * Log a message with a given log level to the Minecraft logfile
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @see Level
     */
    void log(Level level, String message);

    /**
     * Log a message with a given log level to the Minecraft logfile.
     * The stacktrace of the Throwable object is printed to STDOUT.
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @param t       The throwable object
     * @see Level
     */
    void log(Level level, String message, Throwable t);

    /**
     * Access to the ServiceManager instance and it's functions
     *
     * @return The ServiceManager instance
     */
    ServiceManager getServiceManager();

    ArrayList<Class<?>> getCustomSettingsFiles();

    /**
     * Returns a colored string of the current plugin, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @return The message prefix
     */
    String getMessagePrefix();


    /**
     * Returns a colored string of a given plugin, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @param plugin The plugin
     * @return The message prefix
     */
    String getMessagePrefix(FoundationPlugin plugin);

    /**
     * Returns a colored string with the plugin name, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @param plugin The plugin name
     * @return The message prefix
     */
    String getMessagePrefix(String plugin);

    Messages messages();

    File getPluginFolder();

    PluginDescription getPluginDescription();

    FoundationPlugin getFallbackBedrockPlugin();

    boolean isFallbackBedrockPlugin();

    FoundationPlugin getPlugin(String pluginName);

    BedrockDefaultsConfig getBedrockDefaults();

    BedrockServer getBedrockServer();

    /**
     * Gracefully disable the plugin.
     */
    void disable();

}
