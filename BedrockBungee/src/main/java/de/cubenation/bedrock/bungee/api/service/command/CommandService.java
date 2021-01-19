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

package de.cubenation.bedrock.bungee.api.service.command;

import de.cubenation.bedrock.bungee.api.BasePlugin;
import de.cubenation.bedrock.bungee.api.command.BungeeCommand;
import de.cubenation.bedrock.bungee.api.command.BungeeCommandManager;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.CommandHandler;
import de.cubenation.bedrock.core.command.AbstractCommand;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.exception.ServiceInitException;

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

    private ArrayList<BungeeCommand> commands = new ArrayList<>();

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
            String commandName = this.getPlugin().getPluginDescription().getName();

            pluginCommandManager = new ComplexCommandManager(this.getPlugin(), commandName);

            // Add default commands that all plugins are capable of
            for (AbstractCommand predefinedCommand : getPredefinedCommands(pluginCommandManager)) {
                pluginCommandManager.addCommand(predefinedCommand);
            }

            // Add bungee exclusive commands
            // Todo: Add bungee exclusive commands

            registerCommand(commandName, pluginCommandManager);
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
        BungeeCommandManager commandManager;
        if(handlers.length == 1) {
            // Simple command
            commandManager = new SimpleCommandManager(this.getPlugin(), command);

            Class<?> handler = handlers[0];
            try {
                Constructor<?> constructor = handler.getConstructor(FoundationPlugin.class, de.cubenation.bedrock.core.command.CommandManager.class);
                ((SimpleCommandManager) commandManager).setCommand((AbstractCommand) constructor.newInstance(plugin, commandManager));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            // Complex command
            commandManager = new ComplexCommandManager(this.getPlugin(), command);

            for (Class<?> handler : handlers) {
                try {
                    Constructor<?> constructor = handler.getConstructor(FoundationPlugin.class, de.cubenation.bedrock.core.command.CommandManager.class);
                    ((ComplexCommandManager) commandManager).addCommand((AbstractCommand) constructor.newInstance(plugin, commandManager));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        registerCommand(command, commandManager);
    }

    private void registerCommand(String commandName, BungeeCommandManager commandManager) throws ServiceInitException {
        try {
            BungeeCommand bungeeCommand = new BungeeCommand(getPlugin(), commandManager, commandName);

            this.addCommand(bungeeCommand);
            this.addCommandManager(commandManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceInitException("Can't setup command manager for " + commandName);
        }
    }

    @Override
    protected BasePlugin getPlugin() {
        return (BasePlugin) super.getPlugin();
    }

    protected void addCommandManager(CommandManager manager) {
        this.commandManagers.add(manager);
    }

    protected void addCommand(BungeeCommand command) {
        getPlugin().getProxy().getPluginManager().registerCommand(getPlugin(), command);

        this.commands.add(command);
    }

    @Override
    public ArrayList<CommandManager> getCommandManagers() {
        return commandManagers;
    }

    @Override
    public String toString() {
        return "CommandService{" +
                "commandManagers=" + commandManagers +
                ", commands=" + commands +
                '}';
    }
}

