package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.CommandRole;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.service.permission.PermissionService;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.permission
 */
public class PermissionListCommand extends Command {

    public PermissionListCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("permission.list", CommandRole.ADMIN));
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

            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "permission.list.header"
                    ).getTranslation()
            );

            HashMap<String, ArrayList<String>> permissionDump = permissionService.getPermissionRoleDump();
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
