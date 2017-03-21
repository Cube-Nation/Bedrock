package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
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

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("permission.list", CommandRole.MODERATOR));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[] { "pl", "permslist", "permissionslist" } );
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrock.permissions.list.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        PermissionService permissionService = getCommandManager().getPlugin().getPermissionService();
        if (permissionService != null) {
            HashMap<String, ArrayList<String>> permissionDump = permissionService.getPermissionRoleDump();
            MessageHelper.displayPermissions(plugin, sender, permissionDump);
        } else {
            MessageHelper.noPermission(this.getCommandManager().getPlugin(), sender);
        }
    }

}
