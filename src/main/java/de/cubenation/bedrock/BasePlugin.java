package de.cubenation.bedrock;

import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.ServiceManager;
import de.cubenation.bedrock.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.service.command.CommandService;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFileService;
import de.cubenation.bedrock.service.localization.LocalizationService;
import de.cubenation.bedrock.service.permission.PermissionService;
import de.cubenation.bedrock.service.pluginconfig.PluginConfigService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public abstract class BasePlugin extends JavaPlugin {

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

        getLogger().setLevel(Level.FINEST);

        // initialize service manager
        this.serviceManager = new ServiceManager(this);

        // DO NOT MODIFY THIS ORDER!
        try {
            // register plugin config service
            this.serviceManager.registerService("pluginconfig", new PluginConfigService(this));

            // register color scheme service
            this.serviceManager.registerService("colorscheme", new ColorSchemeService(this));

        } catch (ServiceInitException e) {
            this.disable(e);
        }

        // call onPreEnable after registering the plugin config service
        try {
            this.onPreEnable();
        } catch (Exception e) {
            this.disable(e);
        }


        try {
            // register custom configuration file service
            this.serviceManager.registerService("customconfigurationfile", new CustomConfigurationFileService(this));

            // register command service
            this.serviceManager.registerService("command", new CommandService(this));

            // register localization service
            this.serviceManager.registerService("localization", new LocalizationService(this));

            // register permission service
            this.serviceManager.registerService("permission", new PermissionService(this));

        } catch (ServiceInitException e) {
            this.disable(e);
        }

        // initialize commands
        // TODO: move to command service
        if (getCommandManager() != null) {
            for (CommandManager manager : getCommandManager()) {
                manager.getPluginCommand().setExecutor(manager);
                manager.getPluginCommand().setTabCompleter(manager);
            }
        }

        // TODO this can be removed once the command service works
        this.getPermissionService().saveUnregisteredPermissions();


        // after commands have been initialized, permissions need to be reloaded
        try {
            this.getPermissionService().reload();
        } catch (ServiceReloadException e) {
            this.disable(e);
        }


        // call onPostEnable after we've set everything up
        try {
            this.onPostEnable();
        } catch (Exception e) {
            this.disable(e);
        }


        // start metrics
        this.enableMetrics();
    }

    protected void onPostEnable() throws Exception { }


    /*
     * Metric
     */
    protected void enableMetrics() {
        if (!this.getConfig().getBoolean("metrics.use")) {
            this.log(Level.WARNING, "Disabling metrics");
            return;
        }

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            this.log(Level.WARNING, "Failed to submit metrics");
        }
    }



    /*
     * Commands
     */
    public abstract ArrayList<CommandManager> getCommandManager();


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
        if (this.getColorSchemeService() == null)
            return "["+plugin+"]";

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
        log(Level.SEVERE, "Unrecoverable error: " + e.getMessage());
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
            //
        }
        return null;
    }


    /*
     * Plugin Config Service
     */
    public PluginConfigService getPluginConfigService() {
        return (PluginConfigService) this.getService("pluginconfig");
    }

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


    /*
     * Custom Configuration File Service
     */
    public CustomConfigurationFileService getCustomConfigurationFileService() {
        return (CustomConfigurationFileService) this.getService("customconfigurationfile");
    }

    public abstract List<CustomConfigurationFile> getCustomConfigurationFiles() throws IOException;

}
