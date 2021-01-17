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

package de.cubenation.bedrock.bungee.plugin.command;

import de.cubenation.bedrock.bungee.api.service.command.CommandManager;
import de.cubenation.bedrock.bungee.wrapper.BungeeChatSender;
import de.cubenation.bedrock.bungee.wrapper.BungeePlayer;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Argument;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Description("command.bedrock.teleport.desc")
@Permission(Name = "teleport.other", Role = Role.ADMIN)
@SubCommand({ "teleport", "tp" })
@Argument(
        Description = "command.bedrock.username_uuid.desc",
        Placeholder = "command.bedrock.username_uuid.ph",
        Optional = false
)
public class BedrockPlayerTeleportCommand extends Command {

    public BedrockPlayerTeleportCommand(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(final BedrockChatSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        CommandSender cs = ((BungeeChatSender) sender).getCommandSender();
        BedrockPlayer player = new BungeePlayer((ProxiedPlayer) cs);
        BedrockPlayer target = plugin.getBedrockServer().getPlayer(args[0]);

        player.sendMessage("Teleport starting...");
        player.teleport(target);
        player.sendMessage("Teleport done!");
    }
}
