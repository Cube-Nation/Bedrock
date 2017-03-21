package de.cubenation.api.bedrock.command.manager;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.translation.JsonMessage;
import de.cubenation.api.bedrock.command.AbstractCommand;
import de.cubenation.api.bedrock.command.predefined.HelpCommand;
import de.cubenation.api.bedrock.exception.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.*;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private BasePlugin plugin;

    private PluginCommand pluginCommand;

    private List<AbstractCommand> commands = new ArrayList<>();

    private HelpCommand helpCommand;


    public CommandManager(BasePlugin plugin, PluginCommand pluginCommand, String helpPrefix) {

        this.plugin = plugin;
        this.pluginCommand = pluginCommand;

        // !? Add doubles?
        this.commands.addAll(commands);
        this.helpCommand = new HelpCommand(plugin, this);
        this.commands.add(helpCommand);
    }

    public void addCommand(AbstractCommand command) {
        this.commands.add(command);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        AbstractCommand commandToExecute = null;
//        try {
        if (args.length <= 0) {
            try {
                helpCommand.preExecute(commandSender, args);
            } catch (Exception e) {
                plugin.getLogger().info("Error while executing help command. Shouldn't happen!");
                e.printStackTrace();
            }
            return true;
        } else {
            for (AbstractCommand cmd : commands) {
                if (cmd.isValidTrigger(args)) {
                    commandToExecute = cmd;

                    if (!commandToExecute.hasPermission(commandSender)) {
                        MessageHelper.insufficientPermission(plugin, commandSender);
                        return true;
                    }

                    // If the execution fails with an exception, the manager will search for another command to execute!
                    try {
                        commandToExecute.preExecute(
                                commandSender,
                                Arrays.copyOfRange(args, commandToExecute.getSubcommands().size(), args.length));

                    } catch (CommandException e) {
                        MessageHelper.commandExecutionError(this.plugin, commandSender, e);
                        e.printStackTrace();

                    } catch (IllegalCommandArgumentException e) {
                        MessageHelper.invalidCommand(this.plugin, commandSender);

                        JsonMessage jsonHelp = commandToExecute.getJsonHelp(commandSender);
                        if (jsonHelp == null) {
                            MessageHelper.insufficientPermission(this.plugin, commandSender);
                        } else {
                            jsonHelp.send(commandSender);
                        }

                        return true;
                    } catch (InsufficientPermissionException e) {
                        MessageHelper.insufficientPermission(this.plugin, commandSender);
                        return true;
                    }
                    return true;
                }
            }
        }

        // unknown command
        MessageHelper.invalidCommand(this.plugin, commandSender);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (AbstractCommand cmd : getCompletionCommands()) {
            if (!cmd.hasPermission(sender)) {
                continue;
            }

            ArrayList tabCom = cmd.getTabCompletion(args, sender);
            if (tabCom != null) {
                list.addAll(tabCom);
            }
        }
        // Remove duplicates.
        Set<String> set = new HashSet<>(list);
        if (set.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(set);
        }
    }

    public List<AbstractCommand> getHelpCommands() {
        ArrayList<AbstractCommand> helpCommands = new ArrayList<>();
        for (AbstractCommand command : getCommands()) {
            if (command.displayInHelp()) {
                helpCommands.add(command);
            }
        }

        // Sorting
        helpCommands.sort(Comparator.comparing(AbstractCommand::getHelpPriority));

        return helpCommands;
    }

    public List<AbstractCommand> getCompletionCommands() {
        ArrayList<AbstractCommand> completionCommands = new ArrayList<>();
        for (AbstractCommand command : getCommands()) {
            if (command.displayInCompletion()) {
                completionCommands.add(command);
            }
        }
        return completionCommands;
    }

    //region Getter
    public BasePlugin getPlugin() {
        return plugin;
    }

    public PluginCommand getPluginCommand() {
        return pluginCommand;
    }

    public List<AbstractCommand> getCommands() {
        return commands;
    }

    //endregion
}