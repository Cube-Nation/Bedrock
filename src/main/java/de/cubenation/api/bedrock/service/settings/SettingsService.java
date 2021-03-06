/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.api.bedrock.service.settings;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.NoSuchPlayerException;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.service.AbstractService;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class SettingsService extends AbstractService {

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
     * @throws ServiceInitException if the initialization fails.
     */
    @Override
    public void init() throws ServiceInitException {
        this.loadSettings();
        if (!settingsMap.isEmpty()) {
            // Create folder if there is a settings file
            this.createDataFolder();
        }
    }

    /**
     * Reload the settings service
     *
     * @throws ServiceReloadException if reloading fails
     * TODO: entry.getValue.reload() never throws an exception
     */
    @Override
    public void reload() throws ServiceReloadException {
        for (Map.Entry<String, SettingsManager> entry : settingsMap.entrySet()) {
            entry.getValue().reload();
        }
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
     * @throws ServiceInitException if the directory could not be created
     */
    private void createDataFolder() throws ServiceInitException {
        if (!this.settingsDirectory.exists() && !this.settingsDirectory.mkdirs())
            throw new ServiceInitException("Could not create folder " + this.settingsDirectory.getName());
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

    public CustomSettingsFile getReadWriteSettings(Class<?> aClass, UUID uuid) throws NoSuchPlayerException {
        SettingsManager settingsManager = settingsClassMap.get(aClass);
        return getOrCreateSettingsForUser(settingsManager, uuid);
    }

    public CustomSettingsFile getReadWriteSettings(Class<?> aClass, Player player) throws NoSuchPlayerException {
        return getReadWriteSettings(aClass, player.getUniqueId());
    }

    public CustomSettingsFile getReadWriteSettings(String key, UUID uuid) throws NoSuchPlayerException {
        SettingsManager settingsManager = settingsMap.get(key);
        return getOrCreateSettingsForUser(settingsManager, uuid);
    }

    public CustomSettingsFile getReadWriteSettings(String key, Player player) throws NoSuchPlayerException {
        return getReadWriteSettings(key, player.getUniqueId());
    }

    public CustomSettingsFile getReadOnlySettings(Class<?> aClass, UUID uuid) throws NoSuchPlayerException {
        SettingsManager settingsManager = settingsClassMap.get(aClass);
        return getSettingsForUser(settingsManager, uuid);
    }

    public CustomSettingsFile getReadOnlySettings(Class<?> aClass, Player player) throws NoSuchPlayerException {
        return getReadOnlySettings(aClass, player.getUniqueId());
    }

    public CustomSettingsFile getReadOnlySettings(String key, UUID uuid) throws NoSuchPlayerException {
        SettingsManager settingsManager = settingsMap.get(key);
        return getSettingsForUser(settingsManager, uuid);
    }

    public CustomSettingsFile getReadOnlySettings(String key, Player player) throws NoSuchPlayerException {
        return getReadOnlySettings(key, player.getUniqueId());
    }

    private CustomSettingsFile getOrCreateSettingsForUser(SettingsManager settingsManager, UUID uuid){
        if (settingsManager == null) {
            return null;
        }

        CustomSettingsFile settings = settingsManager.getSettings(uuid);
        if (settings == null) {
            settings = settingsManager.createSettingsFileForUser(uuid);
        }

        return settings;
    }

    private CustomSettingsFile getSettingsForUser(SettingsManager settingsManager, UUID uuid){
        if (settingsManager == null) {
            return null;
        }

        CustomSettingsFile settings = settingsManager.getSettings(uuid);
        if (settings == null) {
            settings = settingsManager.getDefaultFile();
        }

        return settings;
    }
}
