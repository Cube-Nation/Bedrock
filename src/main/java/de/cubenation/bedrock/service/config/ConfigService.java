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
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class ConfigService extends AbstractService implements ServiceInterface {

    private HashMap<String, CustomConfigurationFile> configuration_files = new HashMap<>();

    public ConfigService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        this.createDataFolder();

        // try to create the plugin config.yml
        CustomConfigurationFile config;
        try {
            this.registerFile("config.yml", this.createPluginConfig(
                    this.getPlugin(),
                    String.format("%s.config.BedrockDefaults",
                            this.getPlugin().getClass().getPackage().getName()
                    ),
                    "config.yml"
            ));
        } catch (InstantiationException ignored) {

            // try to create the config.yml from Bedrock (yes, for this plugin)
            try {
                this.registerFile("config.yml", this.createPluginConfig(
                        this.getPlugin(),
                        BedrockDefaults.class.getName(),
                        "config.yml"
                ));
            } catch (InstantiationException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }

        // next, add all custom files
        this.registerFiles(this.getPlugin().getCustomConfigurationFiles());

        /*
         * now try reading the service.config.do_not_delete_me value. If it does not exist, is empty or has been changed
         * ths default config file will be overwritten merciless *muhaha*
         */
        /*
        if (!this.isValidPluginConfiguration()) {
            this.getPlugin().log(Level.WARNING, "The plugin configuration file config.yml became invalid and will be recreated from scratch");
            try {
                this.writeDefaultBedrockConfig(true);
            } catch (IOException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }
        */
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


    private CustomConfigurationFile createPluginConfig(BasePlugin plugin, String class_name, String name) throws InstantiationException {
        try {
            Class<?> clazz = (Class<?>) Class.forName(class_name);
            Constructor<?> constructor = clazz.getConstructor(BasePlugin.class, String.class);
            return (CustomConfigurationFile) constructor.newInstance(plugin, name);
        } catch (Exception e) {
            throw new InstantiationException("Could not instantiate class " + class_name);
        }
    }

    private void registerFiles(HashMap<String, String> custom_configuration_files) {
        if (custom_configuration_files == null || custom_configuration_files.size() == 0)
            return;

        Iterator it = custom_configuration_files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

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

    public YamlConfiguration getReadOnlyConfig() {
        return getReadOnlyConfig("config.yml");
    }

    public YamlConfiguration getReadOnlyConfig(String name) {
        File file = new File(this.getPlugin().getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + name);
        return (file.exists()) ? YamlConfiguration.loadConfiguration(file) : null;
    }


    public void saveConfig(String name) throws InvalidConfigurationException {
        if (name == null || name.isEmpty())
            return;

        CustomConfigurationFile file = this.configuration_files.get(name);
        if (file == null)
            return;

        this.getPlugin().log(Level.INFO, "  config service: Saving configuration file " + name);
        file.save();
        file.reload();
    }

    /*
    private void writeDefaultBedrockConfig(boolean overwrite) throws IOException {

        String filename = System.getProperty("file.separator") + "config.yml";
        File plugin_config  = new File(this.getPlugin().getDataFolder().getAbsolutePath() + filename);
        File bedrock_config = new File(BedrockPlugin.getInstance().getDataFolder().getAbsolutePath() + filename);

        // Bedrock Plugin is calling
        if (this.getPlugin() instanceof BedrockPlugin) {
            if (!bedrock_config.exists() || overwrite) {
                this.getPlugin().log(Level.INFO, "Creating default configuration from scratch for BedrockPlugin");
                this.getPlugin().saveDefaultConfig();
            }
            return;
        }

        if (!plugin_config.exists() || overwrite) {
            this.getPlugin().log(Level.INFO, "Creating default configuration from scratch for " + this.getPlugin().getDescription().getName());
            Files.copy(
                    bedrock_config.toPath(),
                    plugin_config.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES,
                    LinkOption.NOFOLLOW_LINKS
            );

            this.extendDefaultPluginConfig();
        }
    }

    @SuppressWarnings("deprecation")
    private void extendDefaultPluginConfig() {

        InputStream config_stream = this.getPlugin().getResource("bedrock_defaults.yml");

        // nothing to extend
        if (config_stream == null)
            return;



        final YamlConfiguration plugin_config;
        if (this.getPlugin().getDescription().getAwareness().contains(PluginAwareness.Flags.UTF8) || FileConfiguration.UTF8_OVERRIDE) {
            plugin_config = YamlConfiguration.loadConfiguration(new InputStreamReader(config_stream, Charsets.UTF_8));
        } else {
            final byte[] contents;
            plugin_config = new YamlConfiguration();
            try {
                contents = ByteStreams.toByteArray(config_stream);
            } catch (final IOException e) {
                this.getPlugin().log(Level.SEVERE, "Unexpected failure reading bedrock_defaults.yml", e);
                return;
            }

            final String text = new String(contents, Charset.defaultCharset());
            if (!text.equals(new String(contents, Charsets.UTF_8)))
                this.getPlugin().log(Level.WARNING,
                        "Default system encoding may have misread config.yml from plugin jar"
                );

            try {
                plugin_config.loadFromString(text);
            } catch (final InvalidConfigurationException e) {
                this.getPlugin().log(Level.WARNING,
                        "Cannot load configuration from plugin jar. Cannot extend default configuration", e
                );
            }
        }

        HashMap<String,Object> plugin_config_settings = resolveDefaultPluginConfig(plugin_config);
        for (String key : plugin_config_settings.keySet()) {
            this.getPlugin().log(Level.INFO, String.format("Setting default value of %s to %s",
                    key, plugin_config_settings.get(key).toString()
            ));
            this.getPlugin().getConfig().set(key, plugin_config_settings.get(key));
        }
        this.getPlugin().getConfig().options().copyDefaults(true);
        this.getPlugin().saveConfig();
    }

    private HashMap<String,Object> resolveDefaultPluginConfig(YamlConfiguration config) {
        HashMap<String,Object> data = new HashMap<>();
        for (String key : config.getKeys(true)) {
            try {
                if (config.getConfigurationSection(key).getKeys(false).size() != 0)
                    continue;
            } catch (NullPointerException ignored) {
            }

            data.put(key, config.get(key));
        }
        return data;
    }

    private boolean isValidPluginConfiguration() {
        try {
            return this.getPluginConfig().getString("service.config.do_not_delete_me").equals("Seriously. Do not delete this");
        } catch (NullPointerException e) {
            return false;
        }
    }
*/
}
