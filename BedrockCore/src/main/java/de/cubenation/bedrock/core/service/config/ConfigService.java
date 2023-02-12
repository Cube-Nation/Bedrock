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

package de.cubenation.bedrock.core.service.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.ConfigurationFile;
import de.cubenation.bedrock.core.config.BedrockDefaultsConfig;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.configuration.BedrockYaml;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import lombok.ToString;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public abstract class ConfigService extends AbstractService {

    private final HashMap<Class<?>, CustomConfigurationFile> configurationFiles = new HashMap<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final String do_not_delete_me = "Seriously. Do not delete this";

    public ConfigService(FoundationPlugin plugin) {
        super(plugin);
    }

    /**
     * Initialize the Config Service
     *
     * @throws ServiceInitException if the initialization fails.
     */
    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        createDataFolder();

        // try to create the plugin bedrock.yaml
        try {
            registerFile(BedrockDefaultsConfig.class, instantiatePluginConfig());
        } catch (InstantiationException | InvalidConfigurationException ignored) {

            // try to create the bedrock.yaml from Bedrock (yes, for this plugin)
            try {
                registerFile(BedrockDefaultsConfig.class, instantiateBedrockConfig());
            } catch (InstantiationException | InvalidConfigurationException e) {
                throw new ServiceInitException(e.getMessage());
            }
        }

        // add all custom configuration files by their annotation
        Arrays.stream(plugin.getClass().getAnnotationsByType(ConfigurationFile.class)).forEach(configurationFile -> {
            try {
                registerClass(configurationFile.value());
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Reload the Config Service
     *
     * @throws ServiceReloadException if the reload failed.
     */
    @Override
    public void reload() throws ServiceReloadException {
        for (Map.Entry<Class<?>, CustomConfigurationFile> pair : configurationFiles.entrySet()) {
            Class<?> name = pair.getKey();

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


    /**
     * Create plugin data folder
     *
     * @throws ServiceInitException on error
     */
    private void createDataFolder() throws ServiceInitException {
        // check if plugin data folder exists and create if not
        if (!plugin.getPluginFolder().exists() && !plugin.getPluginFolder().mkdirs()) {
            throw new ServiceInitException("Could not create folder " + plugin.getPluginFolder().getName());
        }
    }


    /**
     * Create bedrock.yaml for the Plugin itself
     * If this fails, the bedrock.yaml is created from the Bedrock Plugin
     *
     * @return CustomConfigurationFile
     * @throws InstantiationException on error
     * @throws InvalidConfigurationException on error
     */
    private CustomConfigurationFile instantiatePluginConfig() throws InstantiationException, InvalidConfigurationException {
        Class<? extends CustomConfigurationFile> clazz = getPluginConfigClass();
        CustomConfigurationFile config = createPluginConfig(
                plugin,
                clazz
        );
        config.init();
        return config;
    }

    /**
     * Create the bedrock.yaml configuration file for this Plugin from Bedrock
     *
     * @return CustomConfigurationFile
     * @throws InstantiationException on error
     * @throws InvalidConfigurationException on error
     */
    private CustomConfigurationFile instantiateBedrockConfig() throws InstantiationException, InvalidConfigurationException {
        CustomConfigurationFile config = createPluginConfig(
                plugin,
                BedrockDefaultsConfig.class
        );
        config.init();
        return config;
    }


    /**
     * Create the plugin configuration file
     *
     * @param plugin        The BasePlugin reference
     * @param configClass   Class name of the CustomConfigurationFile object
     * @return CustomConfiguration File
     * @throws InstantiationException on error
     */
    private CustomConfigurationFile createPluginConfig(FoundationPlugin plugin, Class<? extends CustomConfigurationFile> configClass) throws InstantiationException {
        try {
            Constructor<?> constructor = configClass.getConstructor(FoundationPlugin.class);
            return (CustomConfigurationFile) constructor.newInstance(plugin);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InstantiationException("Could not instantiate class " + configClass + ": " + e.getMessage());
        }
    }

    /**
     * Register a CustomConfigurationFile object from its class
     *
     * @param configClass               class name
     * @throws InstantiationException   on error
     */
    public void registerClass(Class<? extends CustomConfigurationFile> configClass) throws InstantiationException {
        this.registerFile(configClass, createPluginConfig(plugin, configClass));
    }

    /**
     * Register a CustomConfigurationFile object
     *
     * @param configClass     class name
     * @param file      CustomConfigurationFile object
     */
    public void registerFile(Class<? extends CustomConfigurationFile> configClass, CustomConfigurationFile file) {
        if (file == null) {
            return;
        }

        try {
            file.init();
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "  config service: Could not register file for " + configClass.getName(), e);
            return;
        }

        this.configurationFiles.put(configClass, file);
    }


    public CustomConfigurationFile getConfig(Class<?> clazz) {
        if (clazz == null || !configurationFiles.containsKey(clazz)) {
            return null;
        }

        return configurationFiles.get(clazz);
    }

    public abstract BedrockYaml getReadOnlyConfig();

    public abstract BedrockYaml getReadOnlyConfig(String name);

    private Class<? extends CustomConfigurationFile> getPluginConfigClass() throws InstantiationException {
        String className = String.format("%s.config.BedrockDefaults", plugin.getClass().getPackage().getName());

        Class<? extends CustomConfigurationFile> clazz;
        try {
            clazz = (Class<? extends CustomConfigurationFile>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new InstantiationException(String.format("Could not find class %s in plugin %s",
                    className,
                    plugin.getPluginDescription().getName())
            );
        }
        return clazz;
    }
}
