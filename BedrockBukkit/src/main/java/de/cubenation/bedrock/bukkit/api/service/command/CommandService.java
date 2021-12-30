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

package de.cubenation.bedrock.bukkit.api.service.command;

import de.cubenation.bedrock.bukkit.api.BasePlugin;
import de.cubenation.bedrock.bukkit.api.command.BukkitCommandManager;
import de.cubenation.bedrock.bukkit.api.command.predefined.CommandListCommand;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.CommandHandler;
import de.cubenation.bedrock.core.command.AbstractCommand;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.command.predefined.SettingsInfoCommand;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("Duplicates")
public class CommandService extends de.cubenation.bedrock.core.service.command.CommandService {

    private ArrayList<CommandManager> commandManagers = new ArrayList<>();

    public CommandService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // Get all commands and their handles from CommandHandler annotations
        for (CommandHandler commandHandler : this.getPlugin().getClass().getAnnotationsByType(CommandHandler.class)) {
            this.addCommandManager(commandHandler.Command(), commandHandler.Handlers());
        }

        // Try to get plugin command manager
        ComplexCommandManager pluginCommandManager = null;
        for (CommandManager commandManager : commandManagers) {
            if (commandManager.getLabel().equalsIgnoreCase(getPlugin().getPluginDescription().getName())) {
                pluginCommandManager = (ComplexCommandManager) commandManager;
                break;
            }
        }

        // Add plugin command manager in case it's missing
        if (pluginCommandManager == null) {
            PluginCommand command = this.getPlugin().getCommand(this.getPlugin().getPluginDescription().getName());

            pluginCommandManager = new ComplexCommandManager(
                    this.getPlugin(),
                    command.getLabel()
            );

            registerCommand(command, pluginCommandManager);
        }

        // Add default commands that all plugins are capable of
        for (AbstractCommand predefinedCommand : getPredefinedCommands(pluginCommandManager)) {
            pluginCommandManager.addCommand(predefinedCommand);
        }

        // Add bukkit exclusive commands
        pluginCommandManager.addCommand(new CommandListCommand(getPlugin(), pluginCommandManager));

        SettingsService settingService = plugin.getSettingService();
        if (settingService != null && settingService.getSettingsMap() != null && !settingService.getSettingsMap().isEmpty()) {
            pluginCommandManager.addCommand(new SettingsInfoCommand(getPlugin(), pluginCommandManager));
        }
    }

    private void addCommandManager(String command, Class<? extends AbstractCommand>[] handlers) throws ServiceInitException {
        // Invalid command
        if (handlers.length == 0) {
            this.getPlugin().log(
                    Level.WARNING,
                    "CommandService: Not registering command " + command + " (no handlers assigned)"
            );
            return;
        }

        // Register command manager
        PluginCommand pluginCommand = this.getPlugin().getCommand(command);
        BukkitCommandManager manager;
        if(handlers.length == 1) {
            // Simple command
            manager = new SimpleCommandManager(
                    this.getPlugin(),
                    pluginCommand.getLabel()
            );

            Class<?> handler = handlers[0];
            try {
                AbstractCommand instance = null;
                try {
                    Constructor<?> constructor = handler.getConstructor(FoundationPlugin.class, CommandManager.class);
                    instance = (AbstractCommand) constructor.newInstance(plugin, manager);
                    instance.parseExecuteMethod();
                } catch (CommandInitException e) {
                    e.printStackTrace();
                }
                if (instance != null) {
                    ((SimpleCommandManager) manager).setCommand(instance);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            // Complex command
            manager = new ComplexCommandManager(
                    this.getPlugin(),
                    pluginCommand.getLabel()
            );

            for (Class<?> handler : handlers) {
                try {
                    AbstractCommand instance;
                    try {
                        Constructor<?> constructor = handler.getConstructor(FoundationPlugin.class, CommandManager.class);
                        instance = (AbstractCommand) constructor.newInstance(plugin, manager);
                        instance.parseExecuteMethod();
                    } catch (CommandInitException e) {
                        e.printStackTrace();
                        continue;
                    }
                    ((ComplexCommandManager) manager).addCommand(instance);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        registerCommand(pluginCommand, manager);
    }

    private void registerCommand(PluginCommand command, BukkitCommandManager commandManager) throws ServiceInitException {
        try {
            command.setExecutor(commandManager);
            command.setTabCompleter(commandManager);

            this.addCommandManager(commandManager);
        } catch (Exception e) {
            throw new ServiceInitException("Please add your pluginname as command in the plugin.yml!");
        }
    }

    @Override
    protected BasePlugin getPlugin() {
        return (BasePlugin) super.getPlugin();
    }

    protected void addCommandManager(CommandManager manager) {
        this.commandManagers.add(manager);
    }

    @Override
    public ArrayList<CommandManager> getCommandManagers() {
        return this.commandManagers;
    }

    @Override
    public String toString() {
        return "CommandService{" +
                "commandManagers=" + commandManagers +
                '}';
    }
}
