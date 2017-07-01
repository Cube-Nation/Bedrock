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
import de.cubenation.bedrock.bukkit.api.annotation.Argument;
import de.cubenation.bedrock.bukkit.api.annotation.Description;
import de.cubenation.bedrock.bukkit.api.annotation.Permission;
import de.cubenation.bedrock.bukkit.api.annotation.SubCommand;
import de.cubenation.bedrock.bukkit.api.command.Command;
import de.cubenation.bedrock.bukkit.api.command.CommandRole;
import de.cubenation.bedrock.bukkit.api.exception.CommandException;
import de.cubenation.bedrock.bukkit.api.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.bukkit.api.exception.PlayerNotFoundException;
import de.cubenation.bedrock.bukkit.api.helper.MessageHelper;
import de.cubenation.bedrock.bukkit.api.service.command.CommandManager;
import de.cubenation.bedrock.bukkit.api.service.permission.PermissionService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.permissions.desc")
@Permission(Name = "permissions.other", Role = CommandRole.MODERATOR)
@Permission(Name = "permissions.self", Role = CommandRole.USER)
@SubCommand({ "permissions", "perms" })
@Argument(
        Description = "command.bedrock.username_uuid.desc", Placeholder = "command.bedrock.username_uuid.ph", Optional = true,
        Permission = "permissions.other", Role = CommandRole.MODERATOR
)
public class PermissionOtherCommand extends Command {

    public PermissionOtherCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {

        // check args length
        if (args.length > 1)
            throw new IllegalCommandArgumentException();

        String player = (args.length == 0) ? sender.getName() : args[0];

        PermissionService permissionService = getPlugin().getPermissionService();
        if (permissionService == null) {
            MessageHelper.noPermission(this.getCommandManager().getPlugin(), sender);
            return;
        }

        try {
            MessageHelper.displayPermissions(
                    plugin,
                    sender,
                    permissionService.getPermissions(
                            Bukkit.getPlayer(player)
                    )
            );

        } catch (PlayerNotFoundException e) {
            MessageHelper.noSuchPlayer(this.plugin, sender, player);
        }
    }

}
