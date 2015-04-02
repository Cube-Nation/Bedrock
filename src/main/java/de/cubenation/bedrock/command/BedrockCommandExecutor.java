package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class BedrockCommandExecutor implements CommandExecutor, TabCompleter {

    private List<BedrockSubCommand> subCommands;

    private HelpCommand helpCommand = new HelpCommand("help", new ArrayList<String>(){{add("Zeigt die Hilfe an");}});

    public BedrockCommandExecutor(List<BedrockSubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public void registerSubCommand(BedrockSubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<BedrockSubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length == 0) {
            // TODO: Print Error/Help
            commandSender.sendMessage("Zu wenig Argumente");
            return true;
        }

        for (BedrockSubCommand subCommand : subCommands) {
            if (subCommand.isValidTrigger(args[0])) {

                // TODO: CommandSenderType Check?

                if (!subCommand.hasPermission(commandSender)) {
                    commandSender.sendMessage("You don't have permission.");
                    return true;
                }

                if (args.length - 1 >= subCommand.getMinimumArguments()) {
                    try {
                        subCommand.execute(commandSender, label, Arrays.copyOfRange(args, 1, args.length));
                    } catch (CommandException e) {
                        commandSender.sendMessage(e.getMessage());
                    }
                } else {
                    commandSender.sendMessage("Usage: /" + label + " " + subCommand.getName() + " " + subCommand.getArgumentsHelp());
                }

                return true;
            }
        }

        commandSender.sendMessage("Kein SubCommand gefunden.");

        // TODO: Help anzeigen
        try {
            helpCommand.execute(commandSender, label, args);
        } catch (CommandException e) {
            commandSender.sendMessage(e.getMessage());
        }





        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        System.out.println("Command:" + command + "\nAlias:" + alias + "\nargs:" + args);

        ArrayList<String> completionList = new ArrayList<>();

        for (BedrockSubCommand subCommand : subCommands) {
            completionList.add(subCommand.getName());
        }

        return completionList;
    }
}
