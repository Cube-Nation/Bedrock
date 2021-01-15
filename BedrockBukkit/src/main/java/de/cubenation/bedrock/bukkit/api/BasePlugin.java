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

import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.bukkit.api.service.command.CommandService;
import de.cubenation.bedrock.bukkit.api.service.config.ConfigService;
import de.cubenation.bedrock.bukkit.api.service.inventory.InventoryService;
import de.cubenation.bedrock.bukkit.api.service.stats.MetricsLite;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.exception.*;
import de.cubenation.bedrock.core.helper.version.VersionComparator;
import de.cubenation.bedrock.bukkit.api.message.Messages;
import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.config.CustomConfigurationFile;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents a Bedrock-based plugin.
 *
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class BasePlugin extends EbeanPlugin implements FoundationPlugin {

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
            this.onPreEnable();
        } catch (Exception e) {
            this.disable(e);
            return;
        }

        // initialize ServiceManager
        this.serviceManager = new ServiceManager(this);
        try {
            // TODO: tbd
            // DO NOT MODIFY THIS ORDER!
            serviceManager.registerService(ConfigService.class);
            serviceManager.registerService(ColorSchemeService.class);
            serviceManager.setIntentionallyReady(true);
            serviceManager.registerService(LocalizationService.class);
            serviceManager.registerService(SettingsService.class);
            serviceManager.registerService(CommandService.class);
            serviceManager.registerService(PermissionService.class);
            serviceManager.registerService(InventoryService.class);

            this.serviceManager.registerServices();
        } catch (ServiceInitException | ServiceAlreadyExistsException e) {
            this.log(Level.SEVERE, "Loading services failed");
            this.disable(e);
        }

        this.messages = new Messages(this);

        try {
            BedrockDefaults bedrockDefaults = (BedrockDefaults) getConfigService().getConfig(BedrockDefaults.class);
            setupDatabase(bedrockDefaults.getDatabaseConfiguration());
        } catch (Exception e) {
            this.disable(e);
            return;
        }

        // enable bStats metrics
        new MetricsLite(this);

        // call onPostEnable after we've set everything up
        try {
            this.onPostEnable();
        } catch (Exception e) {
            this.disable(e);
        }
    }

    @Override
    public Boolean isDatabaseEnabled() {
        return true;
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
    public JavaPlugin getPlugin(String name) throws NoSuchPluginException {
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
        return this.getMessagePrefix(this);
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
        return this.getMessagePrefix(plugin.getPluginDescription().getName());
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
        try {
            if (this.getColorSchemeService() == null)
                return "[" + plugin + "]";
        } catch (NullPointerException e) {
            return "[" + plugin + "]";
        }

        return
                this.getColorSchemeService().getColorScheme().getFlag() + "[" +
                        this.getColorSchemeService().getColorScheme().getPrimary() + plugin +
                        this.getColorSchemeService().getColorScheme().getFlag() + "]" +
                        ChatColor.RESET;
    }


    @Override
    public Messages messages() {
        return messages;
    }

    public BedrockDefaults getBedrockDefaults() {
        CustomConfigurationFile config = getConfigService().getConfig(BedrockDefaults.class);
        if (config instanceof BedrockDefaults) {
            return (BedrockDefaults) config;
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
                ChatColor.stripColor(String.format("%s %s", this.getMessagePrefix(), message))
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
        log(Level.SEVERE, "Disabling plugin");
        this.getPluginLoader().disablePlugin(this);
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
        sender.sendMessage(this.getMessagePrefix() + "Unrecoverable error. Disabling plugin");
        this.disable(e);
    }

    /**
     * Returns the Bedrock ConfigService object instance.
     * If the ConfigService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ConfigService
     * @see de.cubenation.bedrock.core.service.config.ConfigService
     */
    public ConfigService getConfigService() {
        return (ConfigService) this.getServiceManager().getService(ConfigService.class);
    }

    /**
     * Returns the Bedrock ColorSchemeService object instance.
     * If the ColorSchemeService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ColorSchemeService
     * @see ColorSchemeService
     */
    public ColorSchemeService getColorSchemeService() {
        return (ColorSchemeService) this.getServiceManager().getService(ColorSchemeService.class);
    }

    /**
     * Returns the Bedrock CommandService object instance.
     * If the CommandService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock CommandService
     * @see CommandService
     */
    public CommandService getCommandService() {
        return (CommandService) this.getServiceManager().getService(CommandService.class);
    }

    /**
     * Returns the Bedrock PermissionService object instance.
     * If the PermissionService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock PermissionService
     * @see PermissionService
     */
    public PermissionService getPermissionService() {
        return (PermissionService) this.getServiceManager().getService(PermissionService.class);
    }

    /**
     * Returns the Bedrock LocalizationService object instance.
     * If the LocalizationService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock LocalizationService
     * @see LocalizationService
     */
    public LocalizationService getLocalizationService() {
        return (LocalizationService) this.getServiceManager().getService(LocalizationService.class);
    }

    /**
     * Returns the Bedrock InventoryService object instance.
     * If the InventoryService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock InventoryService
     * @see InventoryService
     */
    @SuppressWarnings("unused")
    public InventoryService getInventoryService() {
        return (InventoryService) this.getServiceManager().getService(InventoryService.class);
    }

    /**
     * Returns the Bedrock SettingsService object instance.
     * If the InventoryService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock SettingsService
     * @see SettingsService
     */
    public SettingsService getSettingService() {
        return (SettingsService) this.getServiceManager().getService(SettingsService.class);
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
        this.logProhibitedAccess("JavaPlugin#getConfig()", "ConfigurationService / CustomConfigurationFile");
        return null;
    }

    @Override
    public final void saveConfig() {
        this.logProhibitedAccess("JavaPlugin#saveConfig()", "ConfigurationService / CustomConfigurationFile");
    }

    @Override
    public final void reloadConfig() {
        this.logProhibitedAccess("JavaPlugin#reloadConfig()", "ConfigurationService / CustomConfigurationFile");
    }

    @SuppressWarnings("SameParameterValue")
    private void logProhibitedAccess(String prohibited, String replacement) {
        this.log(Level.SEVERE, String.format("Access to %s is prohibited. Please use %s instead",
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

        JavaPlugin plugin = this.getPlugin(name);
        if (plugin == null) {
            throw new NoSuchPluginException("Dependency error: Could not find plugin " + name);
        }

        VersionComparator cmp = new VersionComparator();
        String plugin_version = this.getPlugin("Yamler").getDescription().getVersion();

        if (plugin_version.matches(".+-.+"))
            plugin_version = plugin_version.split("-")[0];

        int result = cmp.compare(plugin_version, version);
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
        try {
            return (FoundationPlugin) getPlugin(PLUGIN_NAME);
        } catch (NoSuchPluginException e) {
            return null;
        }
    }

    @Override
    public boolean isFallbackBedrockPlugin() {
        return getName().equalsIgnoreCase(PLUGIN_NAME);
    }

    @Override
    public Collection<? extends BedrockPlayer> getOnlinePlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(o -> new BukkitPlayer(o)).collect(Collectors.toList());
    }
}
