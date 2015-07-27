package de.cubenation.bedrock;

import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.ServiceManager;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFileService;
import de.cubenation.bedrock.service.localization.LocalizationService;
import de.cubenation.bedrock.service.permission.PermissionService;
import de.cubenation.bedrock.style.ColorScheme;
import de.cubenation.bedrock.style.scheme.DefaultColorScheme;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
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

    private String explicitPermissionPrefix;

    private ServiceManager serviceManager;

    private ColorScheme scheme;

    public BasePlugin() {
        super();
    }


    /*
     * Plugin enabling
     */
    protected void onPreEnable() throws Exception { }

    @Override
    public final void onEnable() {
        this.setupConfig();

        // set default color scheme
        this.setColorScheme(ColorScheme.ColorSchemeName.DEFAULT);

        try {
            this.onPreEnable();
        } catch (Exception e) {
            this.disable(e);
        }

        // start metrics
        this.enableMetrics();

        // initialize services
        try {
            this.initServiceManager();
        } catch (ServiceInitException e) {
            this.disable(e);
        }

        // initialize commands
        this.initCommands();

        // after commands have been initialized, permissions need to be reloaded
        if (this.usePermissionService())
            try {
                this.getPermissionService().reload();
            } catch (ServiceReloadException e) {
                this.disable(e);
            }

        try {
            this.onPostEnable();
        } catch (Exception e) {
            this.disable(e);
        }
    }

    protected void onPostEnable() throws Exception { }


    /*
     * Metric
     */
    protected void enableMetrics() {
        if (!this.getConfig().getBoolean("metrics.use")) {
            this.getLogger().warning("Disabling metrics");
            return;
        }

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            this.getLogger().warning("Failed to submit metrics");
        }
    }

    /*
     * Setup Plugin Configuration
     */
    private void setupConfig() {
        try {
            // check if plugin data folder exists and create if not
            if (!getDataFolder().exists() && !getDataFolder().mkdirs())
                throw new IOException("Could not create folder " + getDataFolder().getName());

            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                log(Level.INFO, "config.yml not found, creating!");
                saveDefaultConfig();
            }

        } catch (Exception e) {
            this.disable(e);
        }
    }



    /*
     * Commands
     */
    public abstract ArrayList<CommandManager> getCommandManager();

    private void initCommands() {
        if (getCommandManager() != null) {
            for (CommandManager manager : getCommandManager()) {
                manager.getPluginCommand().setExecutor(manager);
                manager.getPluginCommand().setTabCompleter(manager);
            }
        }
    }


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
        return getMessagePrefix(this.getDescription().getName());
    }

    public String getMessagePrefix(String plugin) {
        return
                this.scheme.getFlag() + "[" +
                this.scheme.getPrimary() + plugin +
                this.scheme.getFlag() + "]" +
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
     * Services
     */
    public void initServiceManager() throws ServiceInitException {
        this.serviceManager = new ServiceManager(this);

        // register permission service
        if (this.usePermissionService()) {
           this.serviceManager.registerService(
                   "permission",
                   new PermissionService(this)
           );
        }


        // register custom configuration file service
        List<CustomConfigurationFile> ccfiles = null;
        try {
            ccfiles = this.getCustomConfigurationFiles();
        } catch (IOException e) {
            this.disable(e);
        }

        this.serviceManager.registerService(
                "customconfigurationfile",
                new CustomConfigurationFileService(this, ccfiles)
        );


        // register localization service
        // this needs to be instanciated _after_ the custom configuration file service
        // because a locale file is a custom configuration file
        this.serviceManager.registerService(
                "localization",
                new LocalizationService(this, this.getConfig().getString("service.localization.locale"))
        );
    }

    @SuppressWarnings("unused")
    public ServiceInterface getService(String name) throws UnknownServiceException {
        return this.serviceManager.getService(name);
    }


    /*
     * Permission Service
     */
    public abstract Boolean usePermissionService();

    public PermissionService getPermissionService() {
        if (!this.usePermissionService())
            return null;

        try {
            return (PermissionService) this.getService("permission");
        } catch (UnknownServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void setExplicitPermissionPrefix(String explicitPermissionPrefix) {
        this.explicitPermissionPrefix = explicitPermissionPrefix;
    }

    public String getExplicitPermissionPrefix() {
        if (explicitPermissionPrefix == null) {
            return getName().toLowerCase();
        }
        return explicitPermissionPrefix;
    }


    /*
     * Localization Service
     */
    public LocalizationService getLocalizationService() {
        try {
            return (LocalizationService) this.getService("localization");
        } catch (UnknownServiceException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * Custom Configuration File Service
     */
    public CustomConfigurationFileService getCustomConfigurationFileService() {
        try {
            return (CustomConfigurationFileService) this.getService("customconfigurationfile");
        } catch (UnknownServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public YamlConfiguration getCustomConfigurationFile(String filename) throws NoSuchRegisterableException {
        return this.getCustomConfigurationFileService().get(filename);
    }
    
    public abstract List<CustomConfigurationFile> getCustomConfigurationFiles() throws IOException;



    /*
     * Color Scheme
     */
    public ColorScheme getColorScheme() {
        // avoid NullPointerException
        return (this.scheme == null) ? new DefaultColorScheme(this) : this.scheme;
    }

    public void setColorScheme(ColorScheme.ColorSchemeName name) {
        this.scheme = ColorScheme.getColorScheme(this, name);
    }

}
