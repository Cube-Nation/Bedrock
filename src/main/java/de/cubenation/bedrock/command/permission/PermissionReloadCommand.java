package de.cubenation.bedrock.command.permission;

import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
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
        super(new ArrayList<String[]>() {{
                  add(new String[]{"permission", "perm"});
                  add(new String[]{"reload", "r"});
              }},
                new String[]{"Reload all Permissions."},
                "permission.reload");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        getCommandManager().getPlugin().getPermissionService().reloadPermissions();
        sender.sendMessage(getCommandManager().getPlugin().getMessagePrefix() + " Permission reload complete.");
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }
}
