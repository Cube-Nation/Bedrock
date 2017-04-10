package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.CommandDescription;
import de.cubenation.api.bedrock.annotation.CommandPermission;
import de.cubenation.api.bedrock.annotation.CommandSubCommand;
import de.cubenation.api.bedrock.annotation.CommandSubCommands;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import org.bukkit.command.CommandSender;

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

    @CommandDescription(Ident = "command.bedrock.cmd.list.desc")
    @CommandPermission(Name = "command.list", Role = CommandRole.USER)
    @CommandSubCommands(SubCommands = {
            @CommandSubCommand(Commands = { "command", "cmd" }),
            @CommandSubCommand(Commands = { "list", "l" })
    })
    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

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

        MessageHelper.displayCommandList(plugin, sender, commandList);
    }
}
