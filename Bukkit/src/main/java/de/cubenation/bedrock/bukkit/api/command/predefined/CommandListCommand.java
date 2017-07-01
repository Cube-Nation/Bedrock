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

package de.cubenation.bedrock.bukkit.api.command.predefined;

import de.cubenation.bedrock.bukkit.api.BasePlugin;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.bukkit.api.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.bukkit.api.command.Command;
import de.cubenation.bedrock.core.command.CommandRole;
import de.cubenation.bedrock.bukkit.api.exception.CommandException;
import de.cubenation.bedrock.bukkit.api.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.bukkit.api.exception.InsufficientPermissionException;
import de.cubenation.bedrock.bukkit.api.helper.MessageHelper;
import de.cubenation.bedrock.bukkit.api.service.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.cmd.list.desc")
@Permission(Name = "command.list", Role = CommandRole.USER)
@SubCommand({ "command", "cmd" })
@SubCommand({ "list", "l" })
public class CommandListCommand extends Command {

    public CommandListCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        HashMap<String, String> commandList = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : plugin.getDescription().getCommands().entrySet()) {

            // Skip if the command is the settings command
            if (entry.getKey().equalsIgnoreCase(plugin.getDescription().getName())) {
                continue;
            }

            // Try to get a description for the current command, if it exists.
            try {
                String description = (String) entry.getValue().get("description");
                if (description != null) {
                    commandList.put(entry.getKey(), description);
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            commandList.put(entry.getKey(), "");

        }

        if (commandList.isEmpty()) {
            return;
        }

        MessageHelper.displayCommandList(plugin, sender, commandList);
    }
}
