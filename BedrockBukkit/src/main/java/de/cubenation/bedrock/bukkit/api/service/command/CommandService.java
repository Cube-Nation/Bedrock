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
import de.cubenation.bedrock.bukkit.api.command.BukkitCommandTreeRoot;
import de.cubenation.bedrock.bukkit.api.command.predefined.CommandListCommand;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNode;
import de.cubenation.bedrock.core.command.tree.CommandTreePathItem;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import lombok.ToString;
import org.bukkit.command.PluginCommand;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public class CommandService extends de.cubenation.bedrock.core.service.command.CommandService {

    public CommandService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void registerCommand(CommandTreeNode command, String... labels) throws ServiceInitException {
        for (String label : labels) {
            CommandTreePathItem commandTreePathItem = CommandTreePathItem.create(command, label, labels);
            BukkitCommandTreeRoot root = new BukkitCommandTreeRoot(plugin, commandTreePathItem);
            try {
                PluginCommand bukkitCommand = ((BasePlugin) this.plugin).getCommand(label);
                assert bukkitCommand != null;
                bukkitCommand.setExecutor(root);
                bukkitCommand.setTabCompleter(root);
            } catch (Exception e) {
                throw new ServiceInitException("Please add your pluginname as command in the plugin.yml!");
            }
            CommandTreePathItem item = CommandTreePathItem.create(root, label);
            commandHandlers.put(label, item);
        }
    }

    @Override
    protected void registerPlatformSpecificPluginCommands() throws CommandInitException {
        CommandTreeNestedNode nestedNode = pluginCommandManager.addCommandHandler(CommandTreeNestedNode.class, "command", "cmd");
        nestedNode.addCommandHandler(CommandListCommand.class, "list", "l");
    }
}
