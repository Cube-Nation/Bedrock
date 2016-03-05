package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.CommandRole;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.InsufficientPermissionException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BenediktHr on 15.11.15.
 * Project: Bedrock
 */
public class CommandListCommand extends Command {

    public CommandListCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("command.list", CommandRole.USER.getType()));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[]{"command", "cmd"});
        subcommands.add(new String[]{"list", "l"});
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrock.cmd.list.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {

    }

    @Override
    public void execute(CommandSender sender, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        HashMap<String, String> commandList = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : plugin.getDescription().getCommands().entrySet()) {

            // Skip if the command is the settings command
            if (entry.getKey().equalsIgnoreCase(plugin.getDescription().getName())) {
                continue;
            }

            // Try to get a description for the current command, if it exists.
            try {
                String description = (String) entry.getValue().get("description");
                if (description != null) {
                    commandList.put(entry.getKey(), description);
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            commandList.put(entry.getKey(), "");

        }

        if (commandList.isEmpty()) {
            return;
        }

        MessageHelper.send(plugin, sender, new Translation(
                plugin,
                "plugin.commands.header"
        ).getTranslation());

        for (Map.Entry<String, String> entry : commandList.entrySet()) {
            MessageHelper.send(plugin, sender, new Translation(
                    plugin,
                    "plugin.commands.list",
                    new String[] {
                            "command", entry.getKey(),
                            "description", entry.getValue()
                    }
            ).getTranslation());
        }


        ///TODO - B1acksheep
    }
}
