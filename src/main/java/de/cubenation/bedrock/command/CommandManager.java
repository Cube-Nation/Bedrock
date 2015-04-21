package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private final BasePlugin plugin;

    private List<SubCommand> subCommands = new ArrayList<>();

    private HelpCommand helpCommand = new HelpCommand(this);

    public CommandManager(BasePlugin plugin, List<SubCommand> subCommands) {
        this.plugin = plugin;
        this.subCommands = subCommands;
        this.subCommands.add(helpCommand);
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
            try {
                helpCommand.execute(commandSender, label, args);
            } catch (CommandException e) {
                commandSender.sendMessage(e.getLocalizedMessage());
            }
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.isValidTrigger(args)) {

                if (!subCommand.hasPermission(commandSender)) {
                    commandSender.sendMessage(ChatColor.RED + "You don't have permission.");
                    return true;
                }

                try {
                    subCommand.execute(commandSender, label, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                } catch (CommandException e) {
                    commandSender.sendMessage(e.getMessage());
                } catch (IllegalCommandArgumentException e) {
                    commandSender.sendMessage(e.getMessage());
                }
                return true;
            }
        }

        commandSender.sendMessage(plugin.getMessagePrefix() + ChatColor.RED + " Invalid command");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completionList = new ArrayList<>();

        for (SubCommand subCommand : subCommands) {
            String[] completionCommands = subCommand.getTabCompletionListForArgument(args);
            if (completionCommands != null) {
                System.out.println("completionCommands:" + Arrays.toString(completionCommands));
                for (String completionCommand : completionCommands) {
                    if (!completionList.contains(completionCommand)) {
                        if (!args[args.length - 1].equals("")) {
                            if (completionCommand.startsWith(args[args.length - 1])) {
                                completionList.add(completionCommand);
                            }
                        } else {
                            completionList.add(completionCommand);
                        }
                    }
                }
            }
        }

        if (completionList.isEmpty()) {
            return null;
        }

        return completionList;
    }

    public BasePlugin getPlugin() {
        return plugin;
    }
}