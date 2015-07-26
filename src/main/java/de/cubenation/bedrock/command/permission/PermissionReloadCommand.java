package de.cubenation.bedrock.command.permission;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by B1acksheep on 26.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.permission
 */
public class PermissionReloadCommand extends SubCommand {

    public PermissionReloadCommand() {
        super(
                new ArrayList<String[]>() {{
                    add(new String[] { "permission", "perm" } );
                    add(new String[] { "reload", "r" } );
                }},
                new String[] { "help.permission.reload" },
                "permission.reload"
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {
            getCommandManager().getPlugin().getPermissionService().reload();

        } catch (ServiceReloadException e) {
            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    getCommandManager().getPlugin().getMessagePrefix() + " " + new Translation(
                            BedrockPlugin.getInstance(),
                            "permission.reload.failed"
                    ).getTranslation()
            );
            e.printStackTrace();
        }

        MessageHelper.send(
                getCommandManager().getPlugin(),
                sender,
                getCommandManager().getPlugin().getMessagePrefix() + " " + new Translation(
                        BedrockPlugin.getInstance(),
                        "permission.reload.complete"
                ).getTranslation()
        );
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }
}
