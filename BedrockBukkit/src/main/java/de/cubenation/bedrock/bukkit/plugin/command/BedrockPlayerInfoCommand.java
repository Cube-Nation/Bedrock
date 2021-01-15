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

package de.cubenation.bedrock.bukkit.plugin.command;

import de.cubenation.bedrock.bukkit.api.ebean.BedrockPlayer;
import de.cubenation.bedrock.bukkit.api.helper.BedrockEbeanHelper;
import de.cubenation.bedrock.bukkit.api.service.command.CommandManager;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Argument;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.playerinfo.desc")
@Permission(Name = "info.other", Role = Role.ADMIN)
@SubCommand({ "info", "i" })
@Argument(
        Description = "command.bedrock.username_uuid.desc",
        Placeholder = "command.bedrock.username_uuid.ph",
        Optional = true
)
public class BedrockPlayerInfoCommand extends Command {

    public BedrockPlayerInfoCommand(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(final BedrockChatSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        // check args length
        if (args.length > 1) {
            throw new IllegalCommandArgumentException();
        }

        if (args.length == 0 && sender instanceof ConsoleCommandSender) {
            plugin.messages().mustBePlayer(sender);
            return;
        }

        final String player = (args.length == 0) ? sender.getName() : args[0];

        if (UUIDUtil.isUUID(player)) {
            BedrockEbeanHelper.requestBedrockPlayer(player, bedrockPlayer -> {
                sender.sendMessage("Results: (1)");
                sender.sendMessage(bedrockPlayer.getUsername() + ": " + bedrockPlayer.getLastlogin());
            }, e -> sender.sendMessage("No player found for: " + player));
        } else {
            BedrockEbeanHelper.requestBedrockPlayerForLastKnownName(player, false, bedrockPlayers -> {
                sender.sendMessage("Results: (" + bedrockPlayers.size() + ")");
                for (BedrockPlayer bedrockPlayer : bedrockPlayers) {
                    sender.sendMessage(bedrockPlayer.getUsername() + ": " + bedrockPlayer.getLastlogin());
                }
            }, e -> sender.sendMessage("No player found for: " + player));
        }
    }
}
