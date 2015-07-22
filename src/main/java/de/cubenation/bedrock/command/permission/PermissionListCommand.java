package de.cubenation.bedrock.command.permission;

import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.service.permission.PermissionService;
import net.md_5.bungee.api.ChatColor;
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
public class PermissionListCommand extends SubCommand {

    public PermissionListCommand() {
        super(new ArrayList<String[]>() {{
                  add(new String[]{"permission", "perm"});
                  add(new String[]{"list", "l"});
              }},
                new String[]{"Display all Permissions."},
                "permission.list");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        PermissionService permissionService = getCommandManager().getPlugin().getPermissionService();
        if (permissionService != null) {



            ChatColor sec = getCommandManager().getPlugin().getSecondaryColor();

            sender.sendMessage(getCommandManager().getPlugin().getMessagePrefix() + " All Permissions");

            HashMap<String, ArrayList<String>> permissionDump = permissionService.getPermissionRoleDump();
            for (Map.Entry entry : permissionDump.entrySet()) {
                String role = sec + entry.getKey().toString() + ChatColor.WHITE;
                for (String perm :  (ArrayList<String>) entry.getValue()) {
                    role += "\n" + ChatColor.WHITE + " - " + perm;
                }
                sender.sendMessage(role);
            }
        } else {
            sender.sendMessage(getCommandManager().getPlugin().getMessagePrefix() + " No Permissions available.");
        }
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }
}
