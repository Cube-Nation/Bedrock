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

    private HashMap<String, CustomConfigurationFile> configuration_files = new HashMap<>();

    private final String do_not_delete_me = "Seriously. Do not delete this";

    public ConfigService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        this.createDataFolder();

        // try to create the plugin config.yml
        try {
            this.registerFile("config.yml", this.instantiatePluginConfig());
        } catch (InstantiationException | InvalidConfigurationException ignored) {

            // try to create the config.yml from Bedrock (yes, for this plugin)
            try {
                this.registerFile("config.yml", this.instantiateBedrockConfig());
            } catch (InstantiationException | InvalidConfigurationException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }

        // next, add all custom files
        this.registerFiles(this.getPlugin().getCustomConfigurationFiles());

        /*
         * now try reading the service.config.do_not_delete_me value. If it does not exist, is empty or has been changed
         * ths default config file will be overwritten merciless *muhaha*
         */
        if (!this.isValidPluginConfiguration()) {
            this.getPlugin().log(Level.WARNING, "The plugin configuration file config.yml became invalid and will be recreated from scratch");
            try {
                this.registerFile("config.yml", this.instantiatePluginConfig());
            } catch (InstantiationException | InvalidConfigurationException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        for (Object o : this.configuration_files.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String name = (String) pair.getKey();
            try {
                this.getPlugin().log(Level.INFO, "  config service: Reloading file " + name);
                ((CustomConfigurationFile) pair.getValue()).reload();
            } catch (InvalidConfigurationException e) {
                this.getPlugin().log(Level.SEVERE, "  config service: Could not reload file " + name + ": " + e.getMessage());

                // try re-creating file
                try {
                    this.getPlugin().log(Level.INFO, "  config service: Recreating missing file " + name);
                    ((CustomConfigurationFile) pair.getValue()).init();
                } catch (InvalidConfigurationException e1) {
                    this.getPlugin().log(Level.SEVERE, "  config service: Could not recreate missing file " + name, e);
                }
            }
        }
    }


    private void createDataFolder() throws ServiceInitException {
        // check if plugin data folder exists and create if not
        if (!this.getPlugin().getDataFolder().exists() && !this.getPlugin().getDataFolder().mkdirs())
            throw new ServiceInitException("Could not create folder " + this.getPlugin().getDataFolder().getName());
    }


    private CustomConfigurationFile instantiatePluginConfig() throws InstantiationException, InvalidConfigurationException {
        CustomConfigurationFile config = this.createPluginConfig(
                this.getPlugin(),
                String.format("%s.config.BedrockDefaults",
                        this.getPlugin().getClass().getPackage().getName()
                ),
                "config.yml"
        );
        config.init();
        return config;
    }

    private CustomConfigurationFile instantiateBedrockConfig() throws InstantiationException, InvalidConfigurationException {
        CustomConfigurationFile config = this.createPluginConfig(
                this.getPlugin(),
                BedrockDefaults.class.getName(),
                "config.yml"
        );
        config.init();
        return config;
    }

    private CustomConfigurationFile createPluginConfig(BasePlugin plugin, String class_name, String name) throws InstantiationException {
        try {
            Class<?> clazz = Class.forName(class_name);
            Constructor<?> constructor = clazz.getConstructor(BasePlugin.class, String.class);
            return (CustomConfigurationFile) constructor.newInstance(plugin, name);
        } catch (Exception e) {
            throw new InstantiationException("Could not instantiate class " + class_name);
        }
    }

    private void registerFiles(HashMap<String, String> custom_configuration_files) {
        if (custom_configuration_files == null || custom_configuration_files.size() == 0)
            return;

        for (Object o : custom_configuration_files.entrySet()) {
            Map.Entry pair = (Map.Entry) o;

            try {
                this.registerFile((String) pair.getKey(), this.createPluginConfig(
                        this.getPlugin(),
                        (String) pair.getValue(),
                        (String) pair.getKey()
                ));
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } // while
    }

    public void registerFile(String name, CustomConfigurationFile file) {
        if (file == null)
            return;

        this.getPlugin().log(Level.INFO, "  config service: Registering configuration file " + name);

        try {
            file.init();
        } catch (InvalidConfigurationException e) {
            this.getPlugin().log(Level.SEVERE, "  config service: Could not register file " + name, e);
            return;
        }

        this.configuration_files.put(name, file);
    }


    public CustomConfigurationFile getConfig(String name) {
        if (name == null || !this.configuration_files.containsKey(name))
            return null;

        return this.configuration_files.get(name);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public YamlConfiguration getReadOnlyConfig() {
        return getReadOnlyConfig("config.yml");
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

}
