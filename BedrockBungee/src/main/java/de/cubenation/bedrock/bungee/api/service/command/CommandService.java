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
import de.cubenation.bedrock.bungee.api.command.predefined.CommandListCommand;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeRoot;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import lombok.ToString;

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
    protected void registerCommand(CommandTreeNode command, String label) throws ServiceInitException {
        try {
            CommandTreeRoot root = new CommandTreeRoot(plugin, command);
            BungeeCommand bungeeCommand = new BungeeCommand(plugin, root, label);
            getPlugin().getProxy().getPluginManager().registerCommand(getPlugin(), bungeeCommand);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceInitException("Can't setup command tree for " + label);
        }
    }

    @Override
    public BasePlugin getPlugin() {
        return ((BasePlugin) plugin);
    }

    @Override
    protected void registerPlatformSpecificCommands() {
        CommandTreeNestedNode nestedNode = pluginCommandManager.addCommandHandler(CommandTreeNestedNode.class, "command", "cmd");
        nestedNode.addCommandHandler(CommandListCommand.class, "list", "l");
    }
}

