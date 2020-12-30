package de.cubenation.bedrock.implementation.bukkit;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.google.common.base.Preconditions;
import de.cubenation.bedrock.core.BasePlugin;
import de.cubenation.bedrock.core.BedrockBasePlugin;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.database.DatabaseConfiguration;
import de.cubenation.bedrock.core.exception.DatabaseSetupException;
import de.cubenation.bedrock.core.exception.DependencyException;
import de.cubenation.bedrock.core.exception.NoSuchPluginException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.helper.version.VersionComparator;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.command.CommandService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import de.cubenation.bedrock.core.service.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.service.inventory.InventoryService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.service.settings.CustomSettingsFile;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import de.cubenation.bedrock.core.service.stats.MetricsLite;
import de.cubenation.bedrock.plugin.BedrockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class BukkitBedrockPlugin extends BukkitBasePlugin implements BedrockBasePlugin {

    /**
     * The ServiceManager object
     */
    private ServiceManager serviceManager;

    /**
     * BasePlugin constructor
     * Calls the JavaPlugin constructor
     *
     * @see JavaPlugin
     */
    public BukkitBedrockPlugin() {
        super();
    }


    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    protected void onPreEnable() throws Exception {
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
            this.serviceManager.registerServices();
        } catch (ServiceInitException e) {
            this.log(Level.SEVERE, "Loading services failed");
            this.disable(e);
        }

        try {
            BedrockDefaults bedrockDefaults = (BedrockDefaults) getConfigService().getConfig(BedrockDefaults.class);
            setupDatabase(bedrockDefaults);
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
     */
    protected void onPostEnable() {
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
     * Returns a colored string with the plugin name, known as the message prefix.
     * The message prefix is colored using the flag and primary colors from the current
     * ColorScheme that the plugin uses.
     * <p>
     * If the ColorSchemeService is not ready yet, the simple plugin name is returned.
     *
     * @param plugin The plugin name
     * @return The message prefix
     */
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

    public BedrockDefaults getBedrockDefaults() {
        CustomConfigurationFile config = getConfigService().getConfig(BedrockDefaults.class);
        if (config instanceof BedrockDefaults) {
            return (BedrockDefaults) config;
        }
        return null;
    }

    /**
     * Returns the Bedrock ConfigService object instance.
     * If the ConfigService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ConfigService
     * @see ConfigService
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

        BasePlugin plugin = this.getPlugin(name);
        if (plugin == null) {
            throw new NoSuchPluginException("Dependency error: Could not find plugin " + name);
        }

        VersionComparator cmp = new VersionComparator();
        String plugin_version = this.getPlugin("Yamler").getPluginVersion();

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

    private EbeanServer ebean = null;

    protected void setupDatabase(BedrockDefaults bedrockDefaults) throws Exception {
        if (isDatabaseEnabled()) {
            ServerConfig db = new ServerConfig();

            db.setDefaultServer(false);
            db.setRegister(false);
            db.setClasses(getDatabaseClasses());
            db.setName(getDescription().getName());
            configureDbConfig(db, bedrockDefaults);

            DataSourceConfig ds = db.getDataSourceConfig();

            ds.setUrl(replaceDatabaseString(ds.getUrl()));
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();

            ClassLoader previous = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(getClassLoader());
            ebean = EbeanServerFactory.create(db);
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>();
    }

    private String replaceDatabaseString(String input) {
        input = input.replaceAll("\\{DIR\\}", getDataFolder().getPath().replaceAll("\\\\", "/") + "/");
        input = input.replaceAll("\\{NAME\\}", getDescription().getName().replaceAll("[^\\w_-]", ""));
        return input;
    }

    public EbeanServer getDatabase() {
        Preconditions.checkState(isDatabaseEnabled(), "Plugin does not have database: true");

        return ebean;
    }

    protected void installDDL() {
        SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(false, gen.generateCreateDdl());
    }

    protected void removeDDL() {
        SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(true, gen.generateDropDdl());
    }

    public void configureDbConfig(ServerConfig config, BedrockDefaults bedrockDefaults) throws DatabaseSetupException {
        Validate.notNull(config, "Config cannot be null");

        DatabaseConfiguration configuration = bedrockDefaults.getDatabaseConfiguration();

        // If this configuration is null, try to get the Bedrocks one
        if (configuration == null) {
            BedrockPlugin plugin = (BedrockPlugin) Bukkit.getServer().getPluginManager().getPlugin("Bedrock");
            if (plugin != null) {
                configuration = plugin.getBedrockDefaults().getDatabaseConfiguration();
                getLogger().log(Level.INFO, "Will use default Bedrock database configuration.");
            }
        }

        // If this configuration is still null, throw
        if (configuration == null) {
            throw new DatabaseSetupException();
        }

        DataSourceConfig ds = new DataSourceConfig();
        ds = configuration.configure(ds);

        if (ds.getDriver().contains("sqlite")) {
            config.setDatabasePlatform(new SQLitePlatform());
            config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        config.setDataSourceConfig(ds);
    }
}
