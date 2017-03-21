package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.PlayerNotFoundException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import de.cubenation.api.bedrock.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public void setPermissions(ArrayList<Permission> permissions) {
        PERMISSIONS_OTHER = new Permission("permissions.other", CommandRole.MODERATOR);

        permissions.add(PERMISSIONS_OTHER);
        permissions.add(new Permission("permissions.self", CommandRole.USER));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[]{"permissions", "perms"});
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrock.permissions.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {
        arguments.add(new Argument("command.bedrock.username_uuid.desc", "command.bedrock.username_uuid.ph", true, PERMISSIONS_OTHER));
    }

    @Override
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


            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "permission.list.header"
                    ).getTranslation()
            );

            for (Map.Entry entry : permissionDump.entrySet()) {

                // send role
                MessageHelper.send(
                        this.getCommandManager().getPlugin(),
                        sender,
                        new Translation(
                                this.getCommandManager().getPlugin(),
                                "permission.list.role",
                                new String[]{"role", entry.getKey().toString()}
                        ).getTranslation()
                );

                for (String perm : (ArrayList<String>) entry.getValue()) {
                    MessageHelper.send(
                            this.getCommandManager().getPlugin(),
                            sender,
                            new Translation(
                                    this.getCommandManager().getPlugin(),
                                    "permission.list.permission",
                                    new String[]{"permission", perm}
                            ).getTranslation()
                    );

                } // for (permission)

            } // for (permissionDump)

            // no permissions
        } else {
            MessageHelper.noPermission(this.getCommandManager().getPlugin(), sender);
        } // if
    }

}
