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

package de.cubenation.bedrock.bungee.api.command.predefined;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.HashMap;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Description("command.bedrock.cmd.list.desc")
@Permission(Name = "command.list", Role = Role.USER)
@SubCommand({"command", "cmd"})
@SubCommand({"list", "l"})
public class CommandListCommand extends Command {

    public CommandListCommand(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    public void execute(BedrockChatSender sender) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        HashMap<String, String> commandList = new HashMap<>();

        for (CommandManager commandManager : getPlugin().getCommandService().getCommandManagers()) {
            if (commandManager.getLabel().equalsIgnoreCase(getPlugin().getPluginDescription().getName())) {
                continue;
            }
            commandList.put(commandManager.getLabel(), "");
        }

        if (commandList.isEmpty())
            return;

        plugin.messages().displayCommandList(sender, commandList);
    }
}
