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

package de.cubenation.bedrock.bungee.api;

import de.cubenation.bedrock.bungee.api.message.Messages;
import de.cubenation.bedrock.bungee.api.service.command.CommandService;
import de.cubenation.bedrock.bungee.api.service.config.ConfigService;
import de.cubenation.bedrock.core.model.BedrockServer;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.BedrockDefaultsConfig;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.command.ArgumentTypeService;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.service.database.DatabaseService;
import de.cubenation.bedrock.core.service.datastore.DatastoreService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BasePlugin extends Plugin implements FoundationPlugin {

    public static final String PLUGIN_NAME = "BungeeBedrock";

    /**
     * The ServiceManager object
     */
    private ServiceManager serviceManager;

    /**
     * The messages instance
     */
    private Messages messages;

    /**
     * BasePlugin constructor
     * Calls the Plugin constructor
     *
     * @see Plugin
     */
    public BasePlugin() {

    }

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    public void onPreEnable() throws Exception {
    }

    @Override
    public void onEnable() {
        try {
            onPreEnable();
        } catch (Exception e) {
            disable(e);
            return;
        }

        setupServiceManager();
        messages = new Messages(this);

        // Call onPostEnable after we've set everything up
        try {
            onPostEnable();
        } catch (Exception e) {
            disable(e);
        }
    }

    private void setupServiceManager() {
        serviceManager = new ServiceManager(this);
        try {
            // DO NOT MODIFY THIS ORDER!

            // Register and init the base services first since they
            // will be needed for the other services as well.
            serviceManager.registerAndInitializeService(ConfigService.class);
            serviceManager.registerAndInitializeService(ColorSchemeService.class);
            serviceManager.registerAndInitializeService(DatabaseService.class);
            serviceManager.registerAndInitializeService(DatastoreService.class);
            serviceManager.setIntentionallyReady(true);

            // Register secondary core services
            serviceManager.registerService(LocalizationService.class);
            serviceManager.registerService(SettingsService.class);
            serviceManager.registerService(ArgumentTypeService.class);
            serviceManager.registerService(CommandService.class);
            serviceManager.registerService(PermissionService.class);
            // TODO: registerService(InventoryService.class);

            // Register custom services
            serviceManager.registerCustomServices();

            // Only at the end init all registered services to circumvent dependency
            // issues in case services depend on each other. Same order as before.
            serviceManager.initServices();
        } catch (ServiceInitException e) {
            log(Level.SEVERE, "Loading services failed");
            disable(e);
        }
    }

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    public void onPostEnable() throws Exception {
    }

    /**
     * Disables this plugin.
     * The exception message is being logged to the Minecraft logfile and the stacktrace
     * is printed to STDOUT.
     *
     * @param e The exception that lead to disabling the plugin
     */
    @SuppressWarnings("WeakerAccess")
    public void disable(Exception e) {
        log(Level.SEVERE, String.format("Unrecoverable error: %s",
                (e.getMessage() == null)
                        ? "No message specified. Please specify a noticeable message when throwing this exception"
                        : e.getMessage()
        ));
        e.printStackTrace();
        disable();
    }

    @Override
    public void disable() {
        log(Level.SEVERE, "Disabling plugin");
        onDisable();
        // TODO: tbd disable
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(
                level,
                ChatColor.stripColor(String.format("%s", message))
        );
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        log(level, message);
        t.printStackTrace();
    }


    @Override
    public ArrayList<Class<?>> getCustomSettingsFiles() {
        return null;
    }

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    /**
     * Returns a colored string of the current plugin, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @return The message prefix
     */
    public String getMessagePrefix() {
        return getMessagePrefix(this);
    }


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
    @SuppressWarnings("WeakerAccess")
    public String getMessagePrefix(FoundationPlugin plugin) {
        return getMessagePrefix(plugin.getPluginDescription().getName());
    }

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
    @SuppressWarnings("WeakerAccess")
    public String getMessagePrefix(String plugin) {
        ColorSchemeService colorSchemeService = (ColorSchemeService) getServiceManager().getService(ColorSchemeService.class);
        try {
            if (colorSchemeService == null) {
                return "[" + plugin + "]";
            }
        } catch (NullPointerException e) {
            return "[" + plugin + "]";
        }

        return colorSchemeService.getColorScheme().getFlag() + "[" +
                colorSchemeService.getColorScheme().getPrimary() + plugin +
                colorSchemeService.getColorScheme().getFlag() + "]" +
                ChatColor.RESET;
    }

    @Override
    public Messages messages() {
        return messages;
    }

    @Override
    public File getPluginFolder() {
        return getDataFolder();
    }

    @Override
    public PluginDescription getPluginDescription() {
        return new PluginDescription(getDescription().getName(),
                getDescription().getMain(),
                getDescription().getVersion(),
                getDescription().getAuthor(),
                getDescription().getDepends(),
                getDescription().getSoftDepends(),
                getDescription().getFile(),
                getDescription().getDescription());
    }

    @Override
    public FoundationPlugin getFallbackBedrockPlugin(){
        return (FoundationPlugin) getProxy().getPluginManager().getPlugin(PLUGIN_NAME);
    }

    @Override
    public boolean isFallbackBedrockPlugin() {
        return getDescription().getName().equalsIgnoreCase(PLUGIN_NAME);
    }

    @Override
    public BedrockDefaultsConfig getBedrockDefaults() {
        ConfigService configService = (ConfigService) getServiceManager().getService(ConfigService.class);
        CustomConfigurationFile config = configService.getConfig(BedrockDefaultsConfig.class);
        if (config instanceof BedrockDefaultsConfig) {
            return (BedrockDefaultsConfig) config;
        }
        return null;
    }

    @Override
    public BedrockServer getBedrockServer() {
        // TODO: ugly
        return new BungeeServer(this);
    }
}
