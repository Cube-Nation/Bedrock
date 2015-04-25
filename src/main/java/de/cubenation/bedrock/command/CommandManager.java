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

    private BasePlugin plugin;

    private List<SubCommand> subCommands = new ArrayList<>();

    private String helpPrefix;

    private HelpCommand helpCommand = new HelpCommand(this, helpPrefix);

    public CommandManager(BasePlugin plugin, List<SubCommand> subCommands) {
        init(plugin, null, subCommands);
    }

    public CommandManager(BasePlugin plugin, String helpPrefix, List<SubCommand> subCommands) {
        init(plugin, helpPrefix, subCommands);
    }

    private void init(BasePlugin plugin, String helpPrefix, List<SubCommand> subCommands) {
        this.plugin = plugin;
        this.helpPrefix = helpPrefix;
        helpCommand.setHelpPrefix(helpPrefix);
        this.subCommands = subCommands;
        this.subCommands.add(helpCommand);

        setCommandManager();
    }

    private void setCommandManager() {
        for (SubCommand subCommand : subCommands) {
            subCommand.setCommandManager(this);
        }
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            try {
                helpCommand.execute(commandSender, label, null, args);
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
                    subCommand.execute(
                            commandSender,
                            label,
                            Arrays.copyOfRange(args, 0, subCommand.getCommands().size() - 1),
                            Arrays.copyOfRange(args, subCommand.getCommands().size(), args.length));

                    return true;
                } catch (CommandException | IllegalCommandArgumentException e) {
                    commandSender.sendMessage(plugin.getMessagePrefix() + e.getMessage());
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
            if (subCommand.hasPermission(sender)) {
                String[] completionCommands = subCommand.getTabCompletionListForArgument(args);
                if (completionCommands != null) {
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
        }

        if (completionList.isEmpty()) {
            return null;
        }

        return completionList;
    }



    //region Getter
    public BasePlugin getPlugin() {
        return plugin;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
    //endregion
}