package de.cubenation.bedrock;

import de.cubenation.bedrock.exception.DependencyException;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.UnknownServiceException;
import de.cubenation.bedrock.helper.version.VersionComparator;
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

    /** tbd */
    private boolean intentionally_ready = false;

    /** The ServiceManager object */
    private ServiceManager serviceManager;

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
     *
     * @throws Exception
     */
    protected void onPreEnable() throws Exception { }

    /**
     * This method is executed when Bukkit enables this plugin.
     * <p>
     * For custom pre- and post-enabling functions use
     *
     *
     */
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

    /**
     * Returns a Bukkit plugin object
     *
     * @param name  The plugin name
     * @return      The requested plugin object
     * @throws      NoSuchPluginException
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
     * Returns a colored string of the current plugin, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @return          The message prefix
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
     * @param plugin    The plugin
     * @return          The message prefix
     */
    public String getMessagePrefix(BasePlugin plugin) {
        return this.getMessagePrefix(plugin.getDescription().getName());
    }

    /**
     * Returns a colored string with the plugin name, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @param plugin    The plugin name
     * @return          The message prefix
     */
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

    /**
     * Log a message with a given log level to the Minecraft logfile
     *
     * @param level     The Log4J log level
     * @param message   The message to log
     * @see             Level
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
     * @param level     The Log4J log level
     * @param message   The message to log
     * @param t         The throwable object
     * @see             Level
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
     * @param e     The exception that lead to disabling the plugin
     */
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
     * @param e         The exception that lead to disabling this plugin
     * @param sender    The CommandSender that needs to be informed
     */
    @SuppressWarnings("unused")
    public void disable(Exception e, CommandSender sender) {
        sender.sendMessage(this.getMessagePrefix() + "Unrecoverable error. Disabling plugin");
        this.disable(e);
    }

    /**
     * Returns a Bedrock Service.
     * If the service is not enabled yet, <code>null</code> is returned
     *
     * @param   name                The service name
     * @return  ServiceInterface
     * @see     ServiceManager
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

    /**
     *
     * Returns the Bedrock ConfigService object instance.
     * If the ConfigService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock ConfigService
     * @see         ConfigService
     */
    public ConfigService getConfigService() {
        return (ConfigService) this.getService("config");
    }

    /**
     *
     * Returns the Bedrock ColorSchemeService object instance.
     * If the ColorSchemeService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock ColorSchemeService
     * @see         ColorSchemeService
     */
    public ColorSchemeService getColorSchemeService() {
        return (ColorSchemeService) this.getService("colorscheme");
    }

    /**
     * Returns the Bedrock CommandService object instance.
     * If the CommandService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock CommandService
     * @see         CommandService
     */
    public CommandService getCommandService() {
        return (CommandService) this.getService("command");
    }

    /**
     * Returns the Bedrock PermissionService object instance.
     * If the PermissionService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock PermissionService
     * @see         PermissionService
     */
    public PermissionService getPermissionService() {
        return (PermissionService) this.getService("permission");
    }

    /**
     * Returns the Bedrock LocalizationService object instance.
     * If the LocalizationService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock LocalizationService
     * @see         LocalizationService
     */
    public LocalizationService getLocalizationService() {
        return (LocalizationService) this.getService("localization");
    }

    /**
     * Set the commands that are handled by this plugin
     *
     * @param commands  A HashMap that contains Strings as keys (the command itself)
     *                  and an ArrayList of Classes that handle this command as HashMap values.
     */
    public abstract void setCommands(HashMap<String,ArrayList<Class<?>>> commands);

    /**
     * Returns a list of CustomConfigurationFiles classes
     *
     * @return  An ArrayList of classes
     * @see     de.cubenation.bedrock.service.config.CustomConfigurationFile
     */
    public abstract ArrayList<Class<?>> getCustomConfigurationFiles();

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

    /**
     * Assert a plugin dependency.
     * If the plugin could not be found a NoSuchPluginException is thrown.
     * If the plugin version dependency is not met, a DependencyException is thrown.
     *
     * @param name      The plugin name
     * @param version   The plugin version
     * @throws          DependencyException
     * @throws          NoSuchPluginException
     */
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

}
