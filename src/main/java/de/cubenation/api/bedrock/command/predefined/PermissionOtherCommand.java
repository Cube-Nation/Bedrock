package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Argument;
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.PlayerNotFoundException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.command.CommandManager;
import de.cubenation.api.bedrock.service.permission.PermissionService;
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
