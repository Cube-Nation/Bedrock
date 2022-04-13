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

package de.cubenation.bedrock.core.service.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.CommandToken;
import de.cubenation.bedrock.core.annotation.HelpMenu;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNode;
import de.cubenation.bedrock.core.command.predefined.*;
import de.cubenation.bedrock.core.command.tree.CommandTreePathItem;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import lombok.Getter;
import lombok.ToString;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public abstract class CommandService extends AbstractService {

    protected CommandTreeNestedNode pluginCommandManager;

    @Getter
    protected final HashMap<String, CommandTreePathItem> commandHandlers = new HashMap<>();

    public CommandService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        try {
            // Create plugin command handler
            String pluginCommandLabel = getPlugin().getPluginDescription().getName().toLowerCase();
            pluginCommandManager = new CommandTreeNestedNode(plugin);
            registerCommand(pluginCommandManager, pluginCommandLabel);

            // Add help command
            pluginCommandManager.addHelpCommand();

            // Add default commands that all plugins are capable of
            registerPredefinedPluginCommands();

            SettingsService settingService = plugin.getSettingService();
            if (settingService != null && settingService.getSettingsMap() != null && !settingService.getSettingsMap().isEmpty()) {
                CommandTreeNestedNode settingsManager = pluginCommandManager.addCommandHandler(CommandTreeNestedNode.class, "settings");
                settingsManager.addCommandHandler(SettingsInfoCommand.class, "info", "i");
            }

            // Add platform exclusive commands
            registerPlatformSpecificPluginCommands();

            // Register custom commands from CommandConfig
            processCommandConfig();
        } catch (CommandInitException e) {
            throw new ServiceInitException(e);
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        // No reloading of commands supported
    }

    private void registerPredefinedPluginCommands() throws CommandInitException {
        pluginCommandManager.addCommandHandler(ReloadCommand.class, "reload", "r");
        pluginCommandManager.addCommandHandler(VersionCommand.class, "version", "v");
        pluginCommandManager.addCommandHandler(PermissionListCommand.class, "permissionslist", "permslist", "pl");
        CommandTreeNestedNode regenerateManager = pluginCommandManager.addCommandHandler(CommandTreeNestedNode.class, "regenerate", "regen");
        regenerateManager.addCommandHandler(RegenerateLocaleCommand.class, "locale");
        pluginCommandManager.addCommandHandler(PermissionOtherCommand.class, "permissions", "perms");
    }

    private void registerCommand(Class<? extends CommandTreeNode> clazz, String... label) throws ServiceInitException {
        try {
            Constructor<? extends CommandTreeNode> constructor = clazz.getConstructor(FoundationPlugin.class);
            CommandTreeNode nodeInstance = constructor.newInstance(plugin);
            registerCommand(nodeInstance, label);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new ServiceInitException(String.format("Command instance '%s' could not be created.", this.getClass().getName()));
        }
    }

    protected abstract void registerCommand(CommandTreeNode command, String... label) throws ServiceInitException;

    protected abstract void registerPlatformSpecificPluginCommands();

    private void processCommandConfig() throws ServiceInitException, CommandInitException {
        Class<?> clazz;
        try {
            clazz = getCommandConfigClass();
        } catch (InstantiationException e) {
            plugin.log(Level.WARNING, "No command config found. Using default config.");
            return;
        }

        registerNestedNode(null, clazz);
    }

    public void registerNestedNode(CommandTreeNestedNode parent, Class<?> clazz) throws ServiceInitException, CommandInitException {
        // Register commands
        registerCommandNodes(parent, clazz);

        // Register sub-nested-nodes
        for (Class<?> subClass : clazz.getClasses()) {
            // Get all labels
            CommandToken[] commandTokens = subClass.getAnnotationsByType(CommandToken.class);
            List<String> labels = new ArrayList<>();
            // ToDo: fix multi level tokens
            Arrays.stream(commandTokens).forEach(t -> labels.addAll(Arrays.stream(t.value()).toList()));
            if (labels.isEmpty()) {
                plugin.log(Level.WARNING, String.format("Skipping '%s' in command config. Needs to have at least one command label.", clazz.getName()));
                return;
            }

            // Create NestedNode
            CommandTreeNestedNode nestedNode;
            if (parent != null) {
                nestedNode = parent.addCommandHandler(CommandTreeNestedNode.class, labels.toArray(String[]::new));
            } else {
                nestedNode = new CommandTreeNestedNode(plugin);
                for (String label : labels) {
                    registerCommand(nestedNode, label);
                }
            }

            HelpMenu helpMenu = subClass.getAnnotation(HelpMenu.class);
            if (helpMenu != null) {
                nestedNode.addHelpCommand();
            }

            // Pass on
            registerNestedNode(nestedNode, subClass);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerCommandNodes(CommandTreeNestedNode parent, Class<?> clazz) throws ServiceInitException, CommandInitException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // Only accept field of type Class
            if (!field.getType().equals(Class.class)) {
                continue;
            }

            // Check if Value is instance of Command.class
            Class<?> value;
            try {
                value = (Class<?>) field.get(null);
            } catch (IllegalAccessException e) {
                continue;
            } catch (NullPointerException e) {
                plugin.log(Level.WARNING, String.format("Skipping '%s' in command config. Needs to be static.", field.getName()));
                continue;
            }
            if (!Command.class.isAssignableFrom(value)) {
                continue;
            }

            // Get all labels
            CommandToken[] commandTokens = field.getAnnotationsByType(CommandToken.class);
            List<String> labels = new ArrayList<>();
            // ToDo: fix multi level tokens
            Arrays.stream(commandTokens).forEach(t -> labels.addAll(Arrays.stream(t.value()).toList()));
            if (labels.isEmpty()) {
                plugin.log(Level.WARNING, String.format("Skipping '%s' in command config. Needs to have at least one command label.", field.getName()));
                continue;
            }

            // Register commands for labels
            if (parent != null) {
                parent.addCommandHandler((Class<? extends CommandTreeNode>) value, labels.toArray(String[]::new));
                continue;
            }
            registerCommand((Class<? extends CommandTreeNode>) value, labels.toArray(String[]::new));
        }
    }

    private Class<?> getCommandConfigClass() throws InstantiationException {
        String class_name = String.format("%s.config.CommandConfig", plugin.getClass().getPackage().getName());

        Class<?> clazz;
        try {
            clazz = Class.forName(class_name);
        } catch (ClassNotFoundException e) {
            throw new InstantiationException(String.format("Could not find class %s in plugin %s",
                    class_name,
                    this.getPlugin().getPluginDescription().getName())
            );
        }
        return clazz;
    }
}

