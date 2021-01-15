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

import de.cubenation.bedrock.bungee.wrapper.BungeePlayer;
import de.cubenation.bedrock.bungee.api.message.Messages;
import de.cubenation.bedrock.bungee.api.service.command.CommandService;
import de.cubenation.bedrock.bungee.api.service.config.ConfigService;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.exception.ServiceAlreadyExistsException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BasePlugin extends EbeanPlugin implements FoundationPlugin {

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
        super();
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
//            this.registerService(InventoryService.class);

            this.serviceManager.registerServices();
        } catch (ServiceInitException | ServiceAlreadyExistsException e) {
            this.log(Level.SEVERE, "Loading services failed");
            this.disable(e);
        }

        this.messages = new Messages(this);

        try {
            BedrockDefaults bedrockDefaults = getBedrockDefaults();
            log(Level.INFO, bedrockDefaults.toString());
            setupDatabase(bedrockDefaults.getDatabaseConfiguration());
        } catch (Exception e) {
            this.disable(e);
            return;
        }

        // call onPostEnable after we've set everything up
        try {
            this.onPostEnable();
        } catch (Exception e) {
            this.disable(e);
        }
    }

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    public void onPostEnable() throws Exception {
    }

    @Override
    public Boolean isDatabaseEnabled() {
        return true;
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
        this.log(level, message);
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

    public CommandService getCommandService() {
        return (CommandService) this.getServiceManager().getService(CommandService.class);
    }

    @Override
    public ConfigService getConfigService() {
        return (ConfigService) this.getServiceManager().getService(ConfigService.class);
    }

    @Override
    public ColorSchemeService getColorSchemeService() {
        return (ColorSchemeService) this.getServiceManager().getService(ColorSchemeService.class);
    }

    @Override
    public PermissionService getPermissionService() {
        return (PermissionService) this.getServiceManager().getService(PermissionService.class);
    }

    @Override
    public LocalizationService getLocalizationService() {
        return (LocalizationService) this.getServiceManager().getService(LocalizationService.class);
    }

    @Override
    public SettingsService getSettingService() {
        return (SettingsService) this.getServiceManager().getService(SettingsService.class);
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
    public BedrockDefaults getBedrockDefaults() {
        CustomConfigurationFile config = getConfigService().getConfig(BedrockDefaults.class);
        if (config instanceof BedrockDefaults) {
            return (BedrockDefaults) config;
        }
        return null;
    }

    @Override
    public Collection<? extends BedrockPlayer> getOnlinePlayers() {
        return getProxy().getPlayers().stream().map(o -> new BungeePlayer(o)).collect(Collectors.toList());
    }

}
