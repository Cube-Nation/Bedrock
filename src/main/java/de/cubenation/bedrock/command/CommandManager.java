package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private List<SubCommand> subCommands;

    private HelpCommand helpCommand = new HelpCommand();

    public CommandManager(List<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length == 0) {
            // TODO: Print Error/Help
            commandSender.sendMessage("Zu wenig Argumente");
            return true;
        }

        for (SubCommand subCommand : subCommands) {
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

        ArrayList<String> completionList = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].equals("")) {
                for (SubCommand subCommand : subCommands) {
                    completionList.add(subCommand.getName());
                }
            } else {
                for (SubCommand subCommand : subCommands) {
                    if (subCommand.getName().startsWith(args[0])) {
                        completionList.add(subCommand.getName());
                    }
                }
            }
        } else {
            // TODO: Handling for SubSubCommand.
        }

        return completionList;
    }
}