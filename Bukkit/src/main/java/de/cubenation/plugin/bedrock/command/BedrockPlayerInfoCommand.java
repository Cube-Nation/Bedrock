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

package de.cubenation.plugin.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Argument;
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.BedrockEbeanHelper;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.helper.UUIDUtil;
import de.cubenation.api.bedrock.service.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.playerinfo.desc")
@Permission(Name = "info.other", Role = CommandRole.ADMIN)
@SubCommand({ "info", "i" })
@Argument(
        Description = "command.bedrock.username_uuid.desc",
        Placeholder = "command.bedrock.username_uuid.ph",
        Optional = true
)
public class BedrockPlayerInfoCommand extends Command {

    public BedrockPlayerInfoCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(final CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        // check args length
        if (args.length > 1) {
            throw new IllegalCommandArgumentException();
        }

        if (args.length == 0 && sender instanceof ConsoleCommandSender) {
            MessageHelper.mustBePlayer(plugin, sender);
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
