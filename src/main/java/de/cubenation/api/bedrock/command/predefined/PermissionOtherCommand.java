package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.*;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.PlayerNotFoundException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 */
public class PermissionOtherCommand extends Command {

    private Permission PERMISSIONS_OTHER;

    public PermissionOtherCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    @CommandDescription(Ident = "command.bedrock.permissions.desc")
    @CommandPermissions(Permissions = {
            @CommandPermission(Name = "permissions.other", Role = CommandRole.MODERATOR),
            @CommandPermission(Name = "permissions.self", Role = CommandRole.USER)
    })
    @CommandSubCommand(Commands = { "permissions", "perms" })
    @CommandArgument(
        Description = "command.bedrock.username_uuid.desc", Placeholder = "command.bedrock.username_uuid.ph", Optional = true,
        Permission = "permissions.other", Role = CommandRole.MODERATOR
    )
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {

        // check args length
        if (args.length > 1)
            throw new IllegalCommandArgumentException();

        String player = (args.length == 0) ? sender.getName() : args[0];

        PermissionService permissionService = getPlugin().getPermissionService();
        if (permissionService != null) {

            HashMap<String, ArrayList<String>> permissionDump;
            try {
                permissionDump = permissionService.getPermissionRoleDump(Bukkit.getPlayer(player));
            } catch (PlayerNotFoundException e) {
                MessageHelper.noSuchPlayer(this.plugin, sender, player);
                return;
            }

            MessageHelper.displayPermissions(plugin, sender, permissionDump);
        } else {
            MessageHelper.noPermission(this.getCommandManager().getPlugin(), sender);
        }
    }

}
