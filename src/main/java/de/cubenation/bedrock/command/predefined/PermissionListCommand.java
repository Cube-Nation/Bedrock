package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.service.permission.PermissionService;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.permission
 */
public class PermissionListCommand extends Command {

    public PermissionListCommand() {
        super(
                new ArrayList<String[]>() {{
                    add(new String[] { "permission", "perm" } );
                    add(new String[] { "list", "l" } );
                }},
                new String[] { "help.permission.list" },
                "permission.list"
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        PermissionService permissionService = getCommandManager().getPlugin().getPermissionService();
        if (permissionService != null) {

            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    getCommandManager().getPlugin().getMessagePrefix() + " " +
                            new Translation(
                                    BedrockPlugin.getInstance(),
                                    "permission.list.header"
                            ).getTranslation()
            );

            HashMap<String, ArrayList<String>> permissionDump = permissionService.getPermissionRoleDump();
            for (Map.Entry entry : permissionDump.entrySet()) {

                // send role
                MessageHelper.send(
                        getCommandManager().getPlugin(),
                        sender,
                        new Translation(
                                BedrockPlugin.getInstance(),
                                "permission.list.role",
                                new String[] { "role", entry.getKey().toString() }
                        ).getTranslation()
                );

                for (String perm : (ArrayList<String>) entry.getValue()) {
                    MessageHelper.send(
                            getCommandManager().getPlugin(),
                            sender,
                            new Translation(
                                    BedrockPlugin.getInstance(),
                                    "permission.list.permission",
                                    new String[] { "permission", perm }
                            ).getTranslation()
                    );

                } // for (permission)

            } // for (permissionDump)

        // no permissions
        } else {
            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            BedrockPlugin.getInstance(),
                            "permission.no_permissions"
                    ).getTranslation()
            );

        } // if
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }
}
