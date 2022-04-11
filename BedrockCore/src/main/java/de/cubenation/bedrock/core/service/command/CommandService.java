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
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNode;
import de.cubenation.bedrock.core.command.predefined.*;
import de.cubenation.bedrock.core.command.tree.CommandTreePathItem;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;

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
        // Get all commands and their handles from CommandHandler annotations
        // TODO: Create commands from config

        // Create plugin command handler
        String pluginCommandLabel = getPlugin().getPluginDescription().getName().toLowerCase();
        pluginCommandManager = new CommandTreeNestedNode(plugin);
        registerCommand(pluginCommandManager, pluginCommandLabel);

        // Add default commands that all plugins are capable of
        registerPredefinedPluginCommands();

        SettingsService settingService = plugin.getSettingService();
        if (settingService != null && settingService.getSettingsMap() != null && !settingService.getSettingsMap().isEmpty()) {
            CommandTreeNestedNode settingsManager = pluginCommandManager.addCommandHandler(CommandTreeNestedNode.class, "settings");
            settingsManager.addCommandHandler(SettingsInfoCommand.class, "info", "i");
        }

        // Add platform exclusive commands
        registerPlatformSpecificPluginCommands();

        // Add help command
        pluginCommandManager.addHelpCommand();
    }

    @Override
    public void reload() throws ServiceReloadException {
        // No reloading of commands supported
    }

    private void registerPredefinedPluginCommands() {
        pluginCommandManager.addCommandHandler(ReloadCommand.class, "reload", "r");
        pluginCommandManager.addCommandHandler(VersionCommand.class, "version", "v");
        pluginCommandManager.addCommandHandler(PermissionListCommand.class, "permissionslist", "permslist", "pl");
        CommandTreeNestedNode regenerateManager = pluginCommandManager.addCommandHandler(CommandTreeNestedNode.class, "regenerate", "regen");
        regenerateManager.addCommandHandler(RegenerateLocaleCommand.class, "locale");
        pluginCommandManager.addCommandHandler(PermissionOtherCommand.class, "permissions", "perms");
    }

    protected abstract void registerCommand(CommandTreeNode command, String... label) throws ServiceInitException;

    protected abstract void registerPlatformSpecificPluginCommands();
}

