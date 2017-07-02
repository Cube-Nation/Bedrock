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
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.CommandHandler;
import de.cubenation.bedrock.core.command.AbstractCommand;
import de.cubenation.bedrock.core.command.predefined.PermissionListCommand;
import de.cubenation.bedrock.core.command.predefined.RegenerateLocaleCommand;
import de.cubenation.bedrock.core.command.predefined.ReloadCommand;
import de.cubenation.bedrock.core.command.predefined.VersionCommand;
import de.cubenation.bedrock.core.exception.ServiceInitException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
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
        // get all commands and their handles from CommandHandler annotations
        for (CommandHandler commandHandler : this.getPlugin().getClass().getAnnotationsByType(CommandHandler.class)) {
            this.addCommandManager(commandHandler.Command(), commandHandler.Handlers());
        }

        // TODO: doc
        CommandManager pluginCommandManager = null;
        for (CommandManager commandManager : commandManagers) {
            if (commandManager.getLabel().equalsIgnoreCase(getPlugin().getPluginDescription().getName())) {
                pluginCommandManager = commandManager;
                break;
            }
        }

        if (pluginCommandManager == null) {
            String commandName = this.getPlugin().getPluginDescription().getName();

            pluginCommandManager = new CommandManager(
                    this.getPlugin(),
                    commandName,
                    this.getPlugin().getPluginDescription().getName()
            );

            // Add default commands that all plugins are capable of
            for (AbstractCommand predefinedCommand : getPredefinedCommands(pluginCommandManager)) {
                pluginCommandManager.addCommand(predefinedCommand);
            }

            // Add bungee exclusive commands
            // Todo:

            try {
                BungeeCommand bungeeCommand = new BungeeCommand(getPlugin(), pluginCommandManager, commandName);

                this.addCommand(bungeeCommand);
                this.addCommandManager(pluginCommandManager);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServiceInitException("Can't setup command manager for " + commandName);
            }
        }
    }

    private void addCommandManager(String command, Class<? extends AbstractCommand>[] handlers) throws ServiceInitException {
        if (handlers.length == 0) {
            this.getPlugin().log(
                    Level.WARNING,
                    "CommandService: Not registering command " + command + " (no handlers assigned)"
            );
            return;
        }

        CommandManager commandManager = new CommandManager(
                this.getPlugin(),
                command,
                this.getPlugin().getPluginDescription().getName()
        );

        for (Class<?> handler : handlers) {
            Constructor<?> constructor;
            try {
                constructor = handler.getConstructor(FoundationPlugin.class, CommandManager.class);
                commandManager.addCommand((AbstractCommand) constructor.newInstance(plugin, commandManager));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        try {
            BungeeCommand bungeeCommand = new BungeeCommand(getPlugin(), commandManager, command);

            this.addCommand(bungeeCommand);
            this.addCommandManager(commandManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceInitException("Can't setup command manager for " + command);
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

