package de.cubenation.bedrock;

import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.ServiceManager;
import de.cubenation.bedrock.service.colorscheme.ColorSchemeService;
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

        // initialize service manager
        this.serviceManager = new ServiceManager(this);

        // register color scheme service
        try {
            this.serviceManager.registerService(
                    "colorscheme",
                    new ColorSchemeService(this)
            );
        } catch (ServiceInitException e) {
            this.disable(e);
        }

        // call onPreEnable after registering the color scheme service
        try {
            this.onPreEnable();
        } catch (Exception e) {
            this.disable(e);
        }


        try {
            // register permission service
            this.serviceManager.registerService(
                    "permission",
                    new PermissionService(this)
            );


            // register custom configuration file service
            this.serviceManager.registerService(
                    "customconfigurationfile",
                    new CustomConfigurationFileService(this)
            );


            // register localization service
            // this needs to be instanciated _after_ the custom configuration file service
            // because a locale file is a custom configuration file
            this.serviceManager.registerService(
                    "localization",
                    new LocalizationService(this)
            );
        } catch (ServiceInitException e) {
            this.disable(e);
        }


        // initialize commands
        if (getCommandManager() != null) {
            for (CommandManager manager : getCommandManager()) {
                manager.getPluginCommand().setExecutor(manager);
                manager.getPluginCommand().setTabCompleter(manager);
            }
        }


        // after commands have been initialized, permissions need to be reloaded
        if (this.usePermissionService()) {
            try {
                this.getPermissionService().reload();
            } catch (ServiceReloadException e) {
                this.disable(e);
            }
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
        return getMessagePrefix(this.getDescription().getName());
    }

    public String getMessagePrefix(String plugin) {
        return
                this.getColorScheme().getFlag() + "[" +
                this.getColorScheme().getPrimary() + plugin +
                this.getColorScheme().getFlag() + "]" +
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
    @SuppressWarnings("unused")
    public ServiceInterface getService(String name) throws UnknownServiceException {
        return this.serviceManager.getService(name);
    }


    /*
     * Color Scheme Service
     */
    public ColorSchemeService getColorSchemeService() {
        try {
            return (ColorSchemeService) this.getService("colorscheme");
        } catch (UnknownServiceException e) {
            e.printStackTrace();
        }
        return null;
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
     * Color Scheme accessories
     * TODO: move to ColorSchemeService
     */
    public ColorScheme getColorScheme() {
        // avoid NullPointerException
        return (this.scheme == null) ? new DefaultColorScheme(this) : this.scheme;
    }

    //FIXME D1rty Nullpointer...
    /*

    Bei neuem Plugin ohne Config fliegt der Fehler.

    java.lang.NullPointerException
	at de.cubenation.bedrock.style.ColorScheme.getColorScheme(ColorScheme.java:43) ~[?:?]
	at de.cubenation.bedrock.BasePlugin.setColorScheme(BasePlugin.java:337) ~[?:?]
	at de.cubenation.bedrock.service.colorscheme.ColorSchemeService.init(ColorSchemeService.java:26) ~[?:?]
	at de.cubenation.bedrock.service.ServiceManager.registerService(ServiceManager.java:26) ~[?:?]
	at de.cubenation.bedrock.BasePlugin.onEnable(BasePlugin.java:64) ~[?:?]
	at org.bukkit.plugin.java.JavaPlugin.setEnabled(JavaPlugin.java:321) ~[spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at org.bukkit.plugin.java.JavaPluginLoader.enablePlugin(JavaPluginLoader.java:340) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at org.bukkit.plugin.SimplePluginManager.enablePlugin(SimplePluginManager.java:405) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at org.bukkit.craftbukkit.v1_8_R3.CraftServer.loadPlugin(CraftServer.java:356) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at org.bukkit.craftbukkit.v1_8_R3.CraftServer.enablePlugins(CraftServer.java:316) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at net.minecraft.server.v1_8_R3.MinecraftServer.s(MinecraftServer.java:414) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at net.minecraft.server.v1_8_R3.MinecraftServer.k(MinecraftServer.java:378) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at net.minecraft.server.v1_8_R3.MinecraftServer.a(MinecraftServer.java:333) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at net.minecraft.server.v1_8_R3.DedicatedServer.init(DedicatedServer.java:263) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
	at net.minecraft.server.v1_8_R3.MinecraftServer.run(MinecraftServer.java:524) [spigot.jar:git-Spigot-6d16e64-3e9b5c9]
     */
    public void setColorScheme(ColorScheme.ColorSchemeName name) {
        this.scheme = (name != null)
                ? ColorScheme.getColorScheme(this, name)
                : ColorScheme.getColorScheme(this, this.getConfig().getString("scheme.name"));
    }

}
