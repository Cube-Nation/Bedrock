package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.PlayerNotFoundException;
import de.cubenation.bedrock.helper.MessageHelper;
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

    public PermissionOtherCommand() {
        super(
                new String[]{"permissions", "perms"},
                "help.permission.other",
                "permission.other",
                new Argument(
                        new Translation(
                                BedrockPlugin.getInstance(),
                                "help.args.username_uuid"
                        ).getTranslation(),
                        "username/uuid"
                )
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {

        // check args length
        if (args.length != 1)
            throw new IllegalCommandArgumentException();

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

            HashMap<String, ArrayList<String>> permissionDump;
            try {
                permissionDump = permissionService.getPermissionRoleDump(Bukkit.getPlayer(args[0]));
            } catch (PlayerNotFoundException e) {
                MessageHelper.noSuchPlayer(this.plugin, sender, args[0]);
                return;
            }

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
            MessageHelper.send(
                    this.getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "permission.no_permissions"
                    ).getTranslation()
            );

        } // if
    }

}
