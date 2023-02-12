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

package de.cubenation.bedrock.bukkit.api;

import de.cubenation.bedrock.bukkit.api.message.Messages;
import de.cubenation.bedrock.bukkit.api.service.command.CommandService;
import de.cubenation.bedrock.bukkit.api.service.config.ConfigService;
import de.cubenation.bedrock.bukkit.api.service.stats.MetricsLite;
import de.cubenation.bedrock.core.model.BedrockServer;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.BedrockDefaultsConfig;
import de.cubenation.bedrock.core.exception.DependencyException;
import de.cubenation.bedrock.core.exception.NoSuchPluginException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.helper.version.VersionComparator;
import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.command.ArgumentTypeService;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.service.database.DatabaseService;
import de.cubenation.bedrock.core.service.datastore.DatastoreService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.service.settings.CustomSettingsFile;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Bedrock-based plugin.
 *
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class BasePlugin extends JavaPlugin implements FoundationPlugin {

    public static final String PLUGIN_NAME = "Bedrock";
    /**
     * The ServiceManager object
     */
    private ServiceManager serviceManager;

    private Messages messages;

    /**
     * BasePlugin constructor
     * Calls the JavaPlugin constructor
     *
     * @see JavaPlugin
     */
    public BasePlugin() {
        super();
    }


    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    public void onPreEnable() throws Exception {
    }

    /**
     * This method is executed when Bukkit enables this plugin.
     * <p>
     * For custom pre- and post-enabling functions use
     *
     * @see ServiceManager
     */
    @Override
    public final void onEnable() {
        try {
            onPreEnable();
        } catch (Exception e) {
            disable(e);
            return;
        }

        // Initialize ServiceManager
        setupServiceManager();

        messages = new Messages(this);

        // Enable bStats metrics
        new MetricsLite(this);

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
            serviceManager.registerAndInitializeService(de.cubenation.bedrock.core.service.config.ConfigService.class, ConfigService.class);
            serviceManager.registerAndInitializeService(ColorSchemeService.class);
            serviceManager.registerAndInitializeService(DatabaseService.class);
            serviceManager.registerAndInitializeService(DatastoreService.class);
            serviceManager.setIntentionallyReady(true);

            // Register secondary core services
            serviceManager.registerService(LocalizationService.class);
            serviceManager.registerService(SettingsService.class);
            serviceManager.registerService(ArgumentTypeService.class);
            serviceManager.registerService(de.cubenation.bedrock.core.service.command.CommandService.class, CommandService.class);
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
     * Returns a Bukkit plugin object
     *
     * @param name The plugin name
     * @return The requested plugin object
     * @throws NoSuchPluginException if a Plugin is missing.
     */
    @SuppressWarnings("unused")
    public JavaPlugin getJavaPlugin(String name) throws NoSuchPluginException {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(name);
        if (plugin == null) {
            throw new NoSuchPluginException(name);
        }
        return plugin;
    }

    /**
     * Access to the ServiceManager instance and it's functions
     *
     * @return The ServiceManager instance
     */
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
    @Override
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
    @Override
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
    @Override
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

    public BedrockDefaultsConfig getBedrockDefaults() {
        ConfigService configService = (ConfigService) getServiceManager().getService(ConfigService.class);
        CustomConfigurationFile config = configService.getConfig(BedrockDefaultsConfig.class);
        if (config instanceof BedrockDefaultsConfig) {
            return (BedrockDefaultsConfig) config;
        }
        return null;
    }

    /**
     * Log a message with a given log level to the Minecraft logfile
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @see Level
     */
    public void log(Level level, String message) {
        Logger.getLogger("Minecraft").log(
                level,
                ChatColor.stripColor(String.format("%s %s", getMessagePrefix(), message))
        );
    }

    /**
     * Log a message with a given log level to the Minecraft logfile.
     * The stacktrace of the Throwable object is printed to STDOUT.
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @param t       The throwable object
     * @see Level
     */
    @SuppressWarnings("unused")
    public void log(Level level, String message, Throwable t) {
        this.log(level, message);
        t.printStackTrace();
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

    /**
     * Disables this plugin.
     * The exception message is being logged to the Minecraft logfile and the stacktrace
     * is printed to STDOUT.
     * <p>
     * A given sender will be informed, that the plugin is being disabled.
     *
     * @param e      The exception that lead to disabling this plugin
     * @param sender The CommandSender that needs to be informed
     */
    @SuppressWarnings("unused")
    public void disable(Exception e, CommandSender sender) {
        sender.sendMessage(getMessagePrefix() + "Unrecoverable error. Disabling plugin");
        disable(e);
    }

    @Override
    public void disable() {
        log(Level.SEVERE, "Disabling plugin");
        getPluginLoader().disablePlugin(this);
    }

    /**
     * Returns a list of CustomSettingsFile classes
     *
     * @return An ArrayList of classes
     * @see CustomSettingsFile
     */
    public ArrayList<Class<?>> getCustomSettingsFiles() {
        return null;
    }

    /**
     * Disabled Bukkit Commands
     */
    @Override
    public final FileConfiguration getConfig() {
        logProhibitedAccess("JavaPlugin#getConfig()", "ConfigurationService / CustomConfigurationFile");
        return null;
    }

    @Override
    public final void saveConfig() {
        logProhibitedAccess("JavaPlugin#saveConfig()", "ConfigurationService / CustomConfigurationFile");
    }

    @Override
    public final void reloadConfig() {
        logProhibitedAccess("JavaPlugin#reloadConfig()", "ConfigurationService / CustomConfigurationFile");
    }

    @SuppressWarnings("SameParameterValue")
    private void logProhibitedAccess(String prohibited, String replacement) {
        log(Level.SEVERE, String.format("Access to %s is prohibited. Please use %s instead",
                prohibited, replacement
        ));
    }

    /**
     * Assert a plugin dependency.
     * If the plugin could not be found a NoSuchPluginException is thrown.
     * If the plugin version dependency is not met, a DependencyException is thrown.
     *
     * @param name    The plugin name
     * @param version The plugin version
     * @throws DependencyException   if a specific version is not installed.
     * @throws NoSuchPluginException if a plugin is missing.
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    protected void assertPluginDependency(String name, String version) throws DependencyException, NoSuchPluginException {

        JavaPlugin plugin = getJavaPlugin(name);
        if (plugin == null) {
            throw new NoSuchPluginException("Dependency error: Could not find plugin " + name);
        }

        VersionComparator cmp = new VersionComparator();
        String pluginVersion = getJavaPlugin("Yamler").getDescription().getVersion();

        if (pluginVersion.matches(".+-.+")) {
            pluginVersion = pluginVersion.split("-")[0];
        }

        int result = cmp.compare(pluginVersion, version);
        if (result < 0)
            throw new DependencyException(String.format(
                    "Dependency error: You need at least version %s of the %s plugin",
                    version,
                    name
            ));
    }

    @Override
    public PluginDescription getPluginDescription() {

        return new PluginDescription(getDescription().getName(),
                getDescription().getMain(),
                getDescription().getVersion(),
                StringUtils.join(getDescription().getAuthors(), ""),
                new HashSet<String>(){{ addAll(getDescription().getDepend());}} ,
                new HashSet<String>(){{ addAll(getDescription().getSoftDepend());}} ,
                null,
                getDescription().getDescription()
        );
    }

    @Override
    public FoundationPlugin getFallbackBedrockPlugin(){
        return getPlugin(PLUGIN_NAME);
    }

    @Override
    public boolean isFallbackBedrockPlugin() {
        return getName().equalsIgnoreCase(PLUGIN_NAME);
    }

    @Override
    public FoundationPlugin getPlugin(String pluginName){
        JavaPlugin pluginInstance;
        try {
            pluginInstance = getJavaPlugin(pluginName);
        } catch (NoSuchPluginException e) {
            return null;
        }
        if (!(pluginInstance instanceof BasePlugin)) {
            return null;
        }
        return (FoundationPlugin) pluginInstance;
    }

    @Override
    public BedrockServer getBedrockServer() {
        // TODO: ugly
        return new BukkitServer();
    }

    @Override
    public File getPluginFolder() {
        return getDataFolder();
    }
}
