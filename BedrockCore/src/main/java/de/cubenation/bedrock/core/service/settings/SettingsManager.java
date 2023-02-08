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

package de.cubenation.bedrock.core.service.settings;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class SettingsManager {

    private final FoundationPlugin plugin;
    private final Class<?> className;
    private final String name;
    private File settingsDirectory;

    private CustomSettingsFile defaultFile;
    private HashMap<UUID, CustomSettingsFile> userSettings;
    private HashMap<String, CustomSettingsFile> _userSettings;

    public SettingsManager(FoundationPlugin plugin, Class<?> className) throws ServiceInitException {
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
        File settingsDir = new File(this.plugin.getPluginFolder(), SettingsService.SETTINGSDIR);
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
    private CustomSettingsFile createSettings(FoundationPlugin plugin, Class class_name, String customName) throws InstantiationException {
        try {

            if (customName == null) {
                Constructor<?> constructor = class_name.getConstructor(FoundationPlugin.class);
                return (CustomSettingsFile) constructor.newInstance(plugin);
            } else {
                Constructor<?> constructor = class_name.getConstructor(FoundationPlugin.class, String.class);
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

