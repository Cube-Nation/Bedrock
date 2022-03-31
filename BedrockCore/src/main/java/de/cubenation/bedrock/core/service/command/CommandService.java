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
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.command.tree.AbstractCommandTreeNestedNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNodeWithHelp;
import de.cubenation.bedrock.core.command.predefined.*;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import lombok.ToString;

import java.util.HashMap;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public abstract class CommandService extends AbstractService {

    protected AbstractCommandTreeNestedNode pluginCommandManager;
    protected final HashMap<String, CommandTreeNode> commandHandlers = new HashMap<>();

    public CommandService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // Get all commands and their handles from CommandHandler annotations
        // TODO: Create commands from config

        // Try to get the plugin-command handler or create a new one
        String pluginCommandLabel = getPlugin().getPluginDescription().getName().toLowerCase();
        pluginCommandManager = (AbstractCommandTreeNestedNode) commandHandlers.getOrDefault(
                pluginCommandLabel,
                new CommandTreeNestedNodeWithHelp(plugin, null)
                );
        registerCommand(pluginCommandManager, pluginCommandLabel);

        // Add default commands that all plugins are capable of
        registerPredefinedCommands();

        // Add platform exclusive commands
        registerPlatformSpecificCommands();

        SettingsService settingService = plugin.getSettingService();
        if (settingService != null && settingService.getSettingsMap() != null && !settingService.getSettingsMap().isEmpty()) {
            CommandTreeNestedNode settingsManager = new CommandTreeNestedNode(plugin, pluginCommandManager);
            settingsManager.addCommandHandler(new SettingsInfoCommand(plugin, settingsManager), "info", "i");
            pluginCommandManager.addCommandHandler(settingsManager, "settings");
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        // No reloading of commands supported
    }

    private void registerPredefinedCommands() {
        pluginCommandManager.addCommandHandler(new ReloadCommand(plugin, pluginCommandManager), "reload", "r");
        pluginCommandManager.addCommandHandler(new VersionCommand(plugin, pluginCommandManager), "version", "v");
        pluginCommandManager.addCommandHandler(new PermissionListCommand(plugin, pluginCommandManager), "pl", "permslist", "permissionslist");
        CommandTreeNestedNode regenerateManager = new CommandTreeNestedNode(plugin, pluginCommandManager);
        regenerateManager.addCommandHandler(new RegenerateLocaleCommand(plugin, regenerateManager), "locale");
        pluginCommandManager.addCommandHandler(regenerateManager, "regenerate", "regen");
        pluginCommandManager.addCommandHandler(new PermissionOtherCommand(plugin, pluginCommandManager), "permissions", "perms");
    }

    protected abstract void registerCommand(CommandTreeNode command, String label) throws ServiceInitException;

    protected abstract void registerPlatformSpecificCommands();
}

