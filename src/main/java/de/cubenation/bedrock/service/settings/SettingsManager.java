package de.cubenation.bedrock.service.settings;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchPlayerException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.helper.UUIDUtil;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by bhruschka on 25.12.16.
 * Project: Bedrock
 */
public class SettingsManager {

    private final BasePlugin plugin;
    private final Class<?> className;
    private final String name;
    private File settingsDirectory;

    private CustomSettingsFile defaultFile;
    private HashMap<String, CustomSettingsFile> userSettings;

    public SettingsManager(BasePlugin plugin, Class<?> className) throws ServiceInitException {
        this.plugin = plugin;
        this.className = className;

        this.name = initDefault();
        createDataFolder();
        loadSettings();
    }

    /**
     * create plugin inventory folder
     *
     * @throws ServiceInitException
     */
    private void createDataFolder() throws ServiceInitException {
        File settingsDir = new File(this.plugin.getDataFolder(), SettingsService.SETTINGSDIR);
        if (!settingsDir.exists()) {
            if (!settingsDir.mkdir()) {
                throw new ServiceInitException("Could not create folder " + settingsDir.getName());
            }
        }

        File file = new File(settingsDir, name);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new ServiceInitException("Could not create folder " + file.getName());
            }
        }

        this.settingsDirectory = file;
    }

    private String initDefault() {
        try {
            CustomSettingsFile settings = createSettings(plugin, className, null);
            this.registerFile(className, settings);

            return settings.getSettingsName();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadSettings() throws ServiceInitException {
        this.userSettings = new HashMap<>();

        if (this.settingsDirectory == null) {
            throw new ServiceInitException("Settings directory is null!");
        }

        File[] files = this.settingsDirectory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.getName().equalsIgnoreCase("_default.yml")) {
                continue;
            }

            try {
                CustomSettingsFile settings = createSettings(plugin, className, file.getName());
                this.userSettings.put(file.getName().replaceAll("\\.yml$", ""), settings);
                registerFile(className, settings);
            } catch (InstantiationException e) {
                plugin.log(Level.WARNING, "Can't load settings for " + file.getName());
            }
        }
    }


    /**
     * Create the settings file
     *
     * @param plugin        The BasePlugin reference
     * @param class_name    Class name of the CustomConfigurationFile object
     * @return CustomSettingsFile
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    private CustomSettingsFile createSettings(BasePlugin plugin, Class class_name, String customName) throws InstantiationException {
        try {

            if (customName == null) {
                Constructor<?> constructor = class_name.getConstructor(BasePlugin.class);
                return (CustomSettingsFile) constructor.newInstance(plugin);
            } else {
                Constructor<?> constructor = class_name.getConstructor(BasePlugin.class, String.class);
                return (CustomSettingsFile) constructor.newInstance(plugin, customName);
            }

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
    public void registerFile(Class<?> clazz, CustomSettingsFile file) {
        if (file == null) {
            return;
        }

        try {
            file.init();
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "  config service: Could not register file for " + clazz.getName(), e);
            return;
        }

        this.defaultFile = file;
    }

    public void reload() {
        for (Map.Entry<String, CustomSettingsFile> pair : userSettings.entrySet()) {

            try {
                pair.getValue().reload();
            } catch (InvalidConfigurationException e) {
                plugin.log(Level.SEVERE, "  config service: Could not reload file " + name + ": " + e.getMessage());

                // try re-creating file
                try {
                    pair.getValue().init();
                } catch (InvalidConfigurationException e1) {
                    plugin.log(Level.SEVERE, "  config service: Could not recreate missing file " + name, e);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public CustomSettingsFile getDefaultFile() {
        return defaultFile;
    }

    public HashMap<String, CustomSettingsFile> getUserSettings() {
        return userSettings;
    }

    public CustomSettingsFile getSettings(String user) throws NoSuchPlayerException {
        if (UUIDUtil.isUUID(user)) {
            return getSettings(UUID.fromString(user));
        } else {
            return getSettings(Bukkit.getPlayer(user));
        }
    }

    public CustomSettingsFile getSettings(Player player) throws NoSuchPlayerException {
        if (player == null) {
            throw new NoSuchPlayerException();
        }

        return getSettings(player.getUniqueId());
    }

    public CustomSettingsFile getSettings(UUID uuid) throws NoSuchPlayerException {
        if (uuid == null) {
            throw new NoSuchPlayerException();
        }

        return userSettings.get(uuid.toString());
    }

    public CustomSettingsFile getSettingsOrDefault(Player player) throws NoSuchPlayerException {
        if (player == null) {
            throw new NoSuchPlayerException();
        }

        CustomSettingsFile settingsFile = getSettings(player.getUniqueId());
        if (settingsFile == null) {
            return defaultFile;
        }
        return settingsFile;
    }

    public CustomSettingsFile getSettingsOrDefault(UUID uuid) throws NoSuchPlayerException {
        if (uuid == null) {
            throw new NoSuchPlayerException();
        }

        CustomSettingsFile settingsFile = userSettings.get(uuid.toString());
        if (settingsFile == null) {
            return defaultFile;
        }
        return settingsFile;
    }

    public CustomSettingsFile createSettingsFileForUser(UUID uuid) {
        try {
            CustomSettingsFile settings = createSettings(plugin, className, uuid.toString() + ".yml");
            if (settings == null) {
                return null;
            }
            this.userSettings.put(uuid.toString(), settings);
            return settings;
        } catch (InstantiationException e) {
            return null;
        }
    }

}
