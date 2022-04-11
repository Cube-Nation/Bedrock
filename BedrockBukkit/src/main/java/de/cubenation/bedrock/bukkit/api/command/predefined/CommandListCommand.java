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
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Description("command.bedrock.cmd.list.desc")
@Permission(Name = "command.list", Role = Role.USER)
public class CommandListCommand extends Command {

    public CommandListCommand(FoundationPlugin plugin) {
        super(plugin);
    }

    public void execute(BedrockChatSender sender) {

        HashMap<String, String> commandList = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : getPlugin().getDescription().getCommands().entrySet()) {

            // Skip if the command is the settings command
            if (entry.getKey().equalsIgnoreCase(getPlugin().getDescription().getName())) {
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

        plugin.messages().displayCommandList(sender, commandList);
    }

    @Override
    public BasePlugin getPlugin() {
        return (BasePlugin) super.getPlugin();
    }
}
