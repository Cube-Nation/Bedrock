package de.cubenation.api.bedrock.service.settings;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.NoSuchPlayerException;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.helper.UUIDUtil;
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
    private HashMap<UUID, CustomSettingsFile> userSettings;
    private HashMap<String, CustomSettingsFile> _userSettings;

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


            this.defaultFile = this.registerFile(className, settings);

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
                String uuidString = file.getName().replaceAll("\\.yml$", "");
                this.userSettings.put(UUID.fromString(uuidString), settings);
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
     * @param clazz class name
     * @param file  CustomConfigurationFile object
     * @return The registered file.
     */
    public CustomSettingsFile registerFile(Class<?> clazz, CustomSettingsFile file) {
        if (file == null) {
            return file;
        }

        try {
            file.init();
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "  config service: Could not register file for " + clazz.getName(), e);
            return null;
        }

        return file;
    }

    public void reload() {
        initDefault();

        for (Map.Entry<UUID, CustomSettingsFile> pair : userSettings.entrySet()) {

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

    public HashMap<UUID, CustomSettingsFile> getUserSettings() {
        return userSettings;
    }

    public CustomSettingsFile getSettings(String user) throws NoSuchPlayerException {
        if (UUIDUtil.isUUID(user)) {
            return getSettings(UUID.fromString(user));
        } else {
            Player player = Bukkit.getPlayer(user);
            if (player == null) {
                throw new NoSuchPlayerException(user);
            }
            return getSettings(player);
        }
    }

    public CustomSettingsFile getSettings(Player player) {
        if (player == null) {
            return null;
        }

        return getSettings(player.getUniqueId());
    }

    public CustomSettingsFile getSettings(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return userSettings.get(uuid);
    }

    public CustomSettingsFile createSettingsFileForUser(UUID uuid) {
        if (userSettings.containsKey(uuid)) {
            return userSettings.get(uuid);
        }


        try {
            CustomSettingsFile settings = createSettings(plugin, className, uuid.toString() + ".yml");
            if (settings == null) {
                return null;
            }
            this.userSettings.put(uuid, settings);
            return settings;
        } catch (InstantiationException e) {
            return null;
        }
    }

}
