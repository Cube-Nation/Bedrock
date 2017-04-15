package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import org.bukkit.command.CommandSender;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 */
@Description("command.bedrock.permissions.list.desc")
@SubCommand({ "pl", "permslist", "permissionslist" })
@Permission(Name = "permission.list", Role = CommandRole.MODERATOR)
public class PermissionListCommand extends Command {

    public PermissionListCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

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
