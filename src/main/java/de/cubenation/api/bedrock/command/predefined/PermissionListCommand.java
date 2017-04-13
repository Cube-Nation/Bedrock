package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.CommandDescription;
import de.cubenation.api.bedrock.annotation.CommandPermission;
import de.cubenation.api.bedrock.annotation.CommandSubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 */
public class PermissionListCommand extends Command {

    public PermissionListCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @CommandDescription("command.bedrock.permissions.list.desc")
    @CommandSubCommand({ "pl", "permslist", "permissionslist" })
    @CommandPermission(Name = "permission.list", Role = CommandRole.MODERATOR)
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        PermissionService permissionService = this.getPlugin().getPermissionService();

        if (permissionService != null) {
            MessageHelper.displayPermissions(
                    plugin,
                    sender,
                    permissionService.getPermissions()
            );

        } else {
            MessageHelper.noPermission(
                    this.getCommandManager().getPlugin(),
                    sender
            );
        }
    }

}
