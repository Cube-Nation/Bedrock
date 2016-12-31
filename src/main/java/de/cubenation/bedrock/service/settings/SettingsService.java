package de.cubenation.bedrock.service.settings;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchPlayerException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bhruschka on 10.11.16.
 * Project: Bedrock
 */
public class SettingsService extends AbstractService implements ServiceInterface {

    public static final String SETTINGSDIR = "settings";
    private File settingsDirectory;

    private HashMap<String, SettingsManager> settingsMap;
    private HashMap<Class<?>, SettingsManager> settingsClassMap;

    public SettingsService(BasePlugin plugin) {
        super(plugin);
        this.settingsDirectory = new File(plugin.getDataFolder() + File.separator + SETTINGSDIR);
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
        this.loadSettings();
    }

    private void loadSettings() {
        settingsMap = new HashMap<>();
        settingsClassMap = new HashMap<>();
        // next, add all custom files
        if (this.getPlugin().getCustomSettingsFiles() != null) {
            for (Class<?> className : this.getPlugin().getCustomSettingsFiles()) {
                try {
                    SettingsManager value = new SettingsManager(getPlugin(), className);
                    settingsMap.put(value.getName(), value);
                    settingsClassMap.put(className, value);
                } catch (ServiceInitException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * create plugin settings folder
     *
     * @throws ServiceInitException
     */
    private void createDataFolder() throws ServiceInitException {
        if (!this.settingsDirectory.exists() && !this.settingsDirectory.mkdirs())
            throw new ServiceInitException("Could not create folder " + this.settingsDirectory.getName());
    }


    @Override
    public void reload() throws ServiceReloadException {
        for (Map.Entry<String, SettingsManager> entry : settingsMap.entrySet()) {
            entry.getValue().reload();
        }
    }

    public HashMap<String, SettingsManager> getSettingsMap() {
        return settingsMap;
    }

    public SettingsManager getSettingsManager(String key) {
        return settingsMap.get(key);
    }

    public SettingsManager getSettingsManager(Class<?> aClass) {
        return settingsClassMap.get(aClass);
    }

    public CustomSettingsFile readSettings(Class<?> aClass, UUID uuid) throws NoSuchPlayerException {
        SettingsManager settingsManager = settingsClassMap.get(aClass);
        if (settingsManager == null) {
            return null;
        }

        return settingsManager.getSettingsOrDefault(uuid);
    }

    public CustomSettingsFile readSettings(Class<?> aClass, Player player) throws NoSuchPlayerException {
        return readSettings(aClass, player.getUniqueId());
    }

    public CustomSettingsFile readSettings(String key, UUID uuid) throws NoSuchPlayerException {
        SettingsManager settingsManager = settingsMap.get(key);
        if (settingsManager == null) {
            return null;
        }

        return settingsManager.getSettingsOrDefault(uuid);
    }

    public CustomSettingsFile readSettings(String key, Player player) throws NoSuchPlayerException {
        return readSettings(key, player.getUniqueId());
    }
}
