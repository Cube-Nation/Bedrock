package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.CommandRole;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.PlayerNotFoundException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.service.permission.PermissionService;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.permission
 */
public class PermissionOtherCommand extends Command {

    public PermissionOtherCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("permissions.self", CommandRole.USER));
        permissions.add(new Permission("permissions.other", CommandRole.ADMIN));
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
        arguments.add(new Argument("command.bedrock.username_uuid.desc", "command.bedrock.username_uuid.ph", true));
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
