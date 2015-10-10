package de.cubenation.bedrock.service.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.config.BedrockDefaults;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigService extends AbstractService implements ServiceInterface {

    private HashMap<Class<?>, CustomConfigurationFile> configuration_files = new HashMap<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final String do_not_delete_me = "Seriously. Do not delete this";

    public ConfigService(BasePlugin plugin) {
        super(plugin);
    }

    /**
     * Initialize the Config Service
     *
     * @throws ServiceInitException
     */
    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        this.createDataFolder();

        // try to create the plugin bedrock.yaml
        try {
            this.registerFile(BedrockDefaults.class, this.instantiatePluginConfig());
        } catch (InstantiationException | InvalidConfigurationException ignored) {

            // try to create the bedrock.yaml from Bedrock (yes, for this plugin)
            try {
                this.registerFile(BedrockDefaults.class, this.instantiateBedrockConfig());
            } catch (InstantiationException | InvalidConfigurationException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }

        // next, add all custom files
        if (this.getPlugin().getCustomConfigurationFiles() != null) {
            for (Class<?> className : this.getPlugin().getCustomConfigurationFiles()) {
                try {
                    this.registerFile(className, this.createPluginConfig(this.getPlugin(), className));

                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
         * now try reading the service.config.do_not_delete_me value. If it does not exist, is empty or has been changed
         * ths default config file will be overwritten merciless *muhaha*
         */
        if (!this.isValidPluginConfiguration()) {
            this.getPlugin().log(Level.WARNING, "The plugin configuration file bedrock.yaml became invalid and will be recreated from scratch");
            try {
                this.registerFile(BedrockDefaults.class, this.instantiatePluginConfig());
            } catch (InstantiationException | InvalidConfigurationException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }
    }

    /**
     * Reload the Config Service
     *
     * @throws ServiceReloadException
     */
    @Override
    public void reload() throws ServiceReloadException {
        for (Object o : this.configuration_files.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String name = (String) pair.getKey();
            try {
                //this.getPlugin().log(Level.INFO, "  config service: Reloading file " + name);
                ((CustomConfigurationFile) pair.getValue()).reload();
            } catch (InvalidConfigurationException e) {
                this.getPlugin().log(Level.SEVERE, "  config service: Could not reload file " + name + ": " + e.getMessage());

                // try re-creating file
                try {
                    //this.getPlugin().log(Level.INFO, "  config service: Recreating missing file " + name);
                    ((CustomConfigurationFile) pair.getValue()).init();
                } catch (InvalidConfigurationException e1) {
                    this.getPlugin().log(Level.SEVERE, "  config service: Could not recreate missing file " + name, e);
                }
            }
        }
    }


    /**
     * create plugin data folder
     *
     * @throws ServiceInitException
     */
    private void createDataFolder() throws ServiceInitException {
        // check if plugin data folder exists and create if not
        if (!this.getPlugin().getDataFolder().exists() && !this.getPlugin().getDataFolder().mkdirs())
            throw new ServiceInitException("Could not create folder " + this.getPlugin().getDataFolder().getName());
    }


    /**
     * Create bedrock.yaml for the Plugin itself
     * If this fails, the bedrock.yaml is created from the Bedrock Plugin
     *
     * @return CustomConfigurationFile
     * @throws InstantiationException
     * @throws InvalidConfigurationException
     */
    private CustomConfigurationFile instantiatePluginConfig() throws InstantiationException, InvalidConfigurationException {
        String class_name = String.format("%s.config.BedrockDefaults", this.getPlugin().getClass().getPackage().getName());
        Class<?> clazz;
        try {
            clazz = Class.forName(class_name);
        } catch (ClassNotFoundException e) {
            throw new InstantiationException(String.format("Could not find class %s in plugin %s",
                    class_name,
                    this.getPlugin().getDescription().getName())
            );
        }

        CustomConfigurationFile config = this.createPluginConfig(
                this.getPlugin(),
                clazz
        );
        config.init();
        return config;
    }

    /**
     * Create the bedrock.yaml configuration file for this Plugin from Bedrock
     *
     * @return CustomConfigurationFile
     * @throws InstantiationException
     * @throws InvalidConfigurationException
     */
    private CustomConfigurationFile instantiateBedrockConfig() throws InstantiationException, InvalidConfigurationException {
        CustomConfigurationFile config = this.createPluginConfig(
                this.getPlugin(),
                BedrockDefaults.class
        );
        config.init();
        return config;
    }


    /**
     * Create the plugin configuration file
     *
     * @param plugin        The BasePlugin reference
     * @param class_name    Class name of the CustomConfigurationFile object
     * @return CustomConfiguration File
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    private CustomConfigurationFile createPluginConfig(BasePlugin plugin, Class class_name) throws InstantiationException {
        try {
            Constructor<?> constructor = class_name.getConstructor(BasePlugin.class);
            return (CustomConfigurationFile) constructor.newInstance(plugin);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InstantiationException("Could not instantiate class " + class_name + ": " + e.getMessage());
        }
    }


    /**
     * Register a CustomConfigurationFile object
     *
     * @param clazz     class name
     * @param file      CustomConfigurationFile object
     */
    public void registerFile(Class<?> clazz, CustomConfigurationFile file) {
        if (file == null)
            return;

        //this.getPlugin().log(Level.INFO, "  config service: Registering configuration file for " + clazz.getName());

        try {
            file.init();
        } catch (InvalidConfigurationException e) {
            this.getPlugin().log(Level.SEVERE, "  config service: Could not register file for " + clazz.getName(), e);
            return;
        }

        this.configuration_files.put(clazz, file);
    }


    public CustomConfigurationFile getConfig(Class<?> clazz) {
        if (clazz == null || !this.configuration_files.containsKey(clazz))
            return null;

        return this.configuration_files.get(clazz);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public YamlConfiguration getReadOnlyConfig() {
        return getReadOnlyConfig(BedrockDefaults.getFilename());
    }

    @Deprecated
    public YamlConfiguration getReadOnlyConfig(String name) {
        File file = new File(this.getPlugin().getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + name);
        return (file.exists()) ? YamlConfiguration.loadConfiguration(file) : null;
    }

    private boolean isValidPluginConfiguration() {
        try {
            return (this.getConfigurationValue("service.config.do_not_delete_me", null).equals(this.do_not_delete_me));
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ConfigService{" +
                "configuration_files=" + configuration_files +
                ", do_not_delete_me='" + do_not_delete_me + '\'' +
                '}';
    }
}