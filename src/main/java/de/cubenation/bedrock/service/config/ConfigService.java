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
import java.util.ArrayList;
import java.util.logging.Level;

public class ConfigService extends AbstractService implements ServiceInterface {

    private ArrayList<CustomConfigurationFile> configuration_files = null;

    public ConfigService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        this.createDataFolder();

        this.configuration_files = this.getPlugin().getCustomConfigurationFiles();
        this.registerFiles();

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
        for (CustomConfigurationFile file : this.configuration_files)
            try {
                this.getPlugin().log(Level.INFO, "Reloading file " + file.getFilename());
                file.reload();
            } catch (InvalidConfigurationException e) {
                this.getPlugin().log(Level.SEVERE, "Could not reload file", e);
            }
    }

    private void createDataFolder() throws ServiceInitException {
        // check if plugin data folder exists and create if not
        if (!this.getPlugin().getDataFolder().exists() && !this.getPlugin().getDataFolder().mkdirs())
            throw new ServiceInitException("Could not create folder " + this.getPlugin().getDataFolder().getName());
    }

    private void registerFiles() {
        // avoid NullPointerException
        if (this.configuration_files == null)
            this.configuration_files = new ArrayList<>();

        boolean has_default = false;

        for (CustomConfigurationFile file : this.configuration_files)
            has_default = this.registerFile(file);

        if (!has_default)
            this.registerFile(new BedrockDefaults(this.getPlugin()));
    }

    public boolean registerFile(CustomConfigurationFile file) {
        boolean was_default = false;
        try {
            this.getPlugin().log(Level.INFO, "Registering configuration file " + file.getFilename());
            file.init();

            if (file.getFilename().equalsIgnoreCase("config.yml"))
                was_default = true;

        } catch (InvalidConfigurationException e) {
            this.getPlugin().log(Level.SEVERE, "Could not initialize configuration file", e);
        }

        return was_default;
    }

    public YamlConfiguration getConfig() {
        return getConfig("config.yml");
    }

    public YamlConfiguration getConfig(String name) {
        File file = new File(this.getPlugin().getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + name);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        if (this.configuration_files == null)
            return;

        for (CustomConfigurationFile file : this.configuration_files) {
            try {
                file.save();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
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
