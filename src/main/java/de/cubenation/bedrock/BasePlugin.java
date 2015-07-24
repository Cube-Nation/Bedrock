package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.ServiceManager;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFile;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFileService;
import de.cubenation.bedrock.service.localization.Locale;
import de.cubenation.bedrock.service.localization.LocalizationServiceInterface;
import de.cubenation.bedrock.service.permission.PermissionServiceInterface;
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

    public BasePlugin() {
        super();
    }


    /*
     * Plugin enabling
     */
    protected void onPreEnable() throws Exception { }

    @Override
    public final void onEnable() {
        setupConfig();

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
        // FIXME: really? pls check
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
            this.getLogger().info(ChatColor.stripColor(this.getMessagePrefix()) + "Disabling metrics");
            return;
        }

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            this.getLogger().info(ChatColor.stripColor(this.getMessagePrefix()) + "Failed to submit metrics");
        }
    }

    /*
     * Setup Plugin Configuration
     */
    private void setupConfig() {
        if (getResource("config.yml") == null) {
            log(Level.INFO, "Save default config");
            try {
                saveDefaultConfig();
            } catch (IllegalArgumentException e) {
                File file = new File(
                        this.getDataFolder().getAbsolutePath() +
                                java.lang.System.getProperty("file.separator") +
                                "config.yml");
                try {
                    new YamlConfiguration().save(file.getAbsolutePath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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
        return getFlagColor() + "[" +
                getPrimaryColor() + this.getDescription().getName() +
                getFlagColor() + "]" +
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
                   new PermissionServiceInterface(this)
           );
        }

        // register localization service
        this.serviceManager.registerService(
                "localization",
                new LocalizationServiceInterface(this, new Locale(this, this.getConfig().getString("locale")))
        );

        // register custom configuration file service
        CustomConfigurationFileService ccf_service = (CustomConfigurationFileService) this.serviceManager.registerService(
                "customconfigurationfile",
                new CustomConfigurationFileService(this)
        );

        for (CustomConfigurationFile file : getCustomConfigurationFiles()) {
            ccf_service.register(file);
        }
    }

    @SuppressWarnings("unused")
    public ServiceInterface getService(String name) throws UnknownServiceException {
        return this.serviceManager.getService(name);
    }


    /*
     * Permission Service
     */
    public abstract Boolean usePermissionService();

    public PermissionServiceInterface getPermissionService() {
        if (!this.usePermissionService())
            return null;

        try {
            return (PermissionServiceInterface) this.getService("permission");
        } catch (UnknownServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    //FIXME unused?
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
    public LocalizationServiceInterface getLocalizationService() {
        try {
            return (LocalizationServiceInterface) this.getService("localization");
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
    
    public YamlConfiguration getCustomConfigurationFile(String filename) throws NoSuchRegisterableException {
        return this.getCustomConfigurationFileService().get(filename);
    }
    
    public abstract ArrayList<CustomConfigurationFile> getCustomConfigurationFiles();



    /*
     * Plugin Colors
     */
    public ChatColor getPrimaryColor() {
        return ChatColor.AQUA;
    }

    public ChatColor getSecondaryColor() {
        return ChatColor.BLUE;
    }

    public ChatColor getFlagColor() {
        return ChatColor.GRAY;
    }


}
