package de.cubenation.bedrock;

import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.UnknownServiceException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.ServiceManager;
import de.cubenation.bedrock.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.service.command.CommandService;
import de.cubenation.bedrock.service.config.ConfigService;
import de.cubenation.bedrock.service.localization.LocalizationService;
import de.cubenation.bedrock.service.metrics.MetricsService;
import de.cubenation.bedrock.service.permission.PermissionService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public abstract class BasePlugin extends JavaPlugin {

    private boolean intentionally_ready = false;

    private ServiceManager serviceManager;

    public BasePlugin() {
        super();
    }

    /*
     * Plugin enabling
     *
     * @throws Exception    may throw any exception
     */
    protected void onPreEnable() throws Exception { }

    @Override
    public final void onEnable() {

        try {
            this.onPreEnable();
        } catch (Exception e) {
            this.disable(e);
            return;
        }

        // initialize service manager
        this.serviceManager = new ServiceManager();

        // DO NOT MODIFY THIS ORDER!
        try {
            // register config service
            this.serviceManager.registerService("config", new ConfigService(this));

            // register color scheme service
            this.serviceManager.registerService("colorscheme", new ColorSchemeService(this));

            this.intentionally_ready = true;

            // register localization service
            this.serviceManager.registerService("localization", new LocalizationService(this));

            // register command service
            this.serviceManager.registerService("command", new CommandService(this));

            // register permission service
            this.serviceManager.registerService("permission", new PermissionService(this));

            // register metrics service
            this.serviceManager.registerService("metrics", new MetricsService(this));

        } catch (ServiceInitException e) {
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

    protected void onPostEnable() throws Exception { }

    /*
     * get a foreign plugin object
     */
    @SuppressWarnings("unused")
    public JavaPlugin getPlugin(String name) throws NoSuchPluginException {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(name);
        if (plugin == null) {
            throw new NoSuchPluginException(name);
        }
        return plugin;
    }


    /*
     * Logging
     */
    public String getMessagePrefix() {
        return this.getMessagePrefix(this);
    }

    public String getMessagePrefix(BasePlugin plugin) {
        return this.getMessagePrefix(plugin.getDescription().getName());
    }

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

    public void log(Level level, String message) {
        Logger.getLogger("Minecraft").log(
                level,
                ChatColor.stripColor(String.format("%s %s", this.getMessagePrefix(), message))
        );
    }

    @SuppressWarnings("unused")
    public void log(Level level, String message, Throwable t) {
        this.log(level, message);
        t.printStackTrace();
    }

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

    @SuppressWarnings("unused")
    public void disable(Exception e, CommandSender sender) {
        sender.sendMessage(this.getMessagePrefix() + "Unrecoverable error. Disabling plugin");
        this.disable(e);
    }


    /*
     * Service access
     */
    public ServiceInterface getService(String name) {
        try {
            return this.serviceManager.getService(name);
        } catch (UnknownServiceException e) {
            if (this.intentionally_ready)
                this.getLogger().log(Level.SEVERE, "[" + this.getDescription().getName() + "] Could not retrieve service: " + name);
        }
        return null;
    }



    /*
     * Plugin Config Service
     */
    public ConfigService getConfigService() {
        return (ConfigService) this.getService("config");
    }

    public abstract ArrayList<Class<?>> getCustomConfigurationFiles();

    /*
     * Color Scheme Service
     */
    public ColorSchemeService getColorSchemeService() {
        return (ColorSchemeService) this.getService("colorscheme");
    }

    /*
     * Command Service
     */
    public CommandService getCommandService() {
        return (CommandService) this.getService("command");
    }

    public abstract void setCommands(HashMap<String,ArrayList<Class<?>>> commands);

    /*
     * Permission Service
     */
    public PermissionService getPermissionService() {
        return (PermissionService) this.getService("permission");
    }

    /*
     * Localization Service
     */
    public LocalizationService getLocalizationService() {
        return (LocalizationService) this.getService("localization");
    }


    /**
     * Disabled Bukkit Commands
     */
    @Override
    public final FileConfiguration getConfig() {
        this.log(Level.SEVERE, "Access to Bukkit getConfig() is prohibited. GTFO!");
        return null;
    }

    @Override
    public final void saveConfig() {
        this.log(Level.SEVERE, "Access to Bukkit saveConfig() is prohibited. GTFO!");
    }

    @Override
    public final void reloadConfig() {
        this.log(Level.SEVERE, "Access to Bukkit reloadConfig() is prohibited. GTFO!");
    }

}
