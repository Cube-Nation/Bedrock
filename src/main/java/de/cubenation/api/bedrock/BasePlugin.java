package de.cubenation.api.bedrock;

import de.cubenation.api.bedrock.exception.DependencyException;
import de.cubenation.api.bedrock.exception.NoSuchPluginException;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.UnknownServiceException;
import de.cubenation.api.bedrock.helper.version.VersionComparator;
import de.cubenation.api.bedrock.reloadable.Reloadable;
import de.cubenation.api.bedrock.service.ServiceInterface;
import de.cubenation.api.bedrock.service.ServiceManager;
import de.cubenation.api.bedrock.service.colorscheme.ColorSchemeService;
import de.cubenation.api.bedrock.service.command.CommandService;
import de.cubenation.api.bedrock.service.config.ConfigService;
import de.cubenation.api.bedrock.service.config.CustomConfigurationFile;
import de.cubenation.api.bedrock.service.inventory.InventoryService;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import de.cubenation.api.bedrock.service.settings.CustomSettingsFile;
import de.cubenation.api.bedrock.service.settings.SettingsService;
import de.cubenation.api.bedrock.service.stats.MetricsLite;
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
 */
public abstract class BasePlugin extends JavaPlugin {

    private static final String SERVICE_CONFIG = "config";
    private static final String SERVICE_COLORSCHEME = "colorscheme";
    private static final String SERVICE_LOCALIZATION = "localization";
    private static final String SERVICE_COMMAND = "command";
    private static final String SERVICE_PERMISSION = "permission";
    private static final String SERVICE_INVENTORY = "inventory";
    private static final String SERVICE_SETTINGS = "settings";

    /** tbd */
    private boolean intentionally_ready = false;

    /** The ServiceManager object */
    private ServiceManager serviceManager;

    private MetricsLite metrics;

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
            this.serviceManager.registerService(SERVICE_CONFIG, new ConfigService(this));

            // register color scheme service
            this.serviceManager.registerService(SERVICE_COLORSCHEME, new ColorSchemeService(this));

            this.intentionally_ready = true;

            // register localization service
            this.serviceManager.registerService(SERVICE_LOCALIZATION, new LocalizationService(this));

            // register settings service
            this.serviceManager.registerService(SERVICE_SETTINGS, new SettingsService(this));

            // register command service
            this.serviceManager.registerService(SERVICE_COMMAND, new CommandService(this));

            // register permission service
            this.serviceManager.registerService(SERVICE_PERMISSION, new PermissionService(this));

            // register inventory service
            this.serviceManager.registerService(SERVICE_INVENTORY, new InventoryService(this));

        } catch (ServiceInitException e) {
            this.disable(e);
            return;
        }

        metrics = new MetricsLite(this);

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
        return (ConfigService) this.getService(SERVICE_CONFIG);
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
        return (ColorSchemeService) this.getService(SERVICE_COLORSCHEME);
    }

    /**
     * Returns the Bedrock CommandService object instance.
     * If the CommandService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock CommandService
     * @see         CommandService
     */
    public CommandService getCommandService() {
        return (CommandService) this.getService(SERVICE_COMMAND);
    }

    /**
     * Returns the Bedrock PermissionService object instance.
     * If the PermissionService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock PermissionService
     * @see         PermissionService
     */
    public PermissionService getPermissionService() {
        return (PermissionService) this.getService(SERVICE_PERMISSION);
    }

    /**
     * Returns the Bedrock LocalizationService object instance.
     * If the LocalizationService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock LocalizationService
     * @see         LocalizationService
     */
    public LocalizationService getLocalizationService() {
        return (LocalizationService) this.getService(SERVICE_LOCALIZATION);
    }

    /**
     * Returns the Bedrock InventoryService object instance.
     * If the InventoryService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock InventoryService
     * @see         InventoryService
     */
    public InventoryService getInventoryService() {
        return (InventoryService) this.getService(SERVICE_INVENTORY);
    }

    /**
     * Returns the Bedrock SettingsService object instance.
     * If the InventoryService is not ready, <code>null</code> is returned.
     *
     * @return      The Bedrock SettingsService
     * @see         SettingsService
     */
    public SettingsService getSettingService() {
        return (SettingsService) this.getService(SERVICE_SETTINGS);
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
     * @see     CustomConfigurationFile
     */
    public abstract ArrayList<Class<?>> getCustomConfigurationFiles();

    /**
     * Returns a list of CustomSettingsFile classes
     *
     * @return  An ArrayList of classes
     * @see     CustomSettingsFile
     */
    public ArrayList<Class<?>> getCustomSettingsFiles() {
        return null;
    }

    /**
     * Returns a list of Reloadable classes
     *
     * @return An ArrayList of classes
     */
    public ArrayList<Reloadable> getReloadable() {
        return null;
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
