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
    @CommandDescription("command.bedrock.permissions.desc")
    @CommandPermissions(Permissions = {
            @CommandPermission(Name = "permissions.other", Role = CommandRole.MODERATOR),
            @CommandPermission(Name = "permissions.self", Role = CommandRole.USER)
    })
    @CommandSubCommand({ "permissions", "perms" })
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
