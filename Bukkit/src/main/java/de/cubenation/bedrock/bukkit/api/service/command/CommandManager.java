/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.bukkit.api.service.command;

import de.cubenation.bedrock.bukkit.api.BasePlugin;
import de.cubenation.bedrock.bukkit.api.command.AbstractCommand;
import de.cubenation.bedrock.bukkit.api.command.predefined.HelpCommand;
import de.cubenation.bedrock.bukkit.api.exception.CommandException;
import de.cubenation.bedrock.bukkit.api.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.bukkit.api.exception.InsufficientPermissionException;
import de.cubenation.bedrock.bukkit.api.helper.MessageHelper;
import de.cubenation.bedrock.bukkit.api.translation.JsonMessage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
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
        // TODO: @blacksheep92 warum!? zu dem zeitpunkt ist das ding eh leer (sollte es sein) - oder überseh ich was?
        this.commands.addAll(commands);
        this.helpCommand = new HelpCommand(plugin, this);
        this.commands.add(helpCommand);
    }

    public void addCommand(AbstractCommand command) {
        this.commands.add(command);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            try {
                helpCommand.preExecute(commandSender, args);
            } catch (Exception e) {
                plugin.getLogger().info("Error while executing help command. Shouldn't happen!");
                e.printStackTrace();
            }
            return true;
        } else {

            // try commands with subcommands
            if (this.tryCommand(
                    this.commands.stream()
                            .filter(abstractCommand -> abstractCommand.getSubcommands().size() != 0)
                            .collect(Collectors.toList()),
                    commandSender,
                    args
            )) {
                return true;
            }

            // try commands without subcommands
            if (this.tryCommand(
                    this.commands.stream()
                            .filter(abstractCommand -> abstractCommand.getSubcommands().size() == 0)
                            .collect(Collectors.toList()),
                    commandSender,
                    args
            )) {
                return true;
            }
        }

        // display help
        ArrayList<AbstractCommand> helpList = new ArrayList<>();
        for (AbstractCommand possibleHelpCommand : getHelpCommands()) {
            if (!(possibleHelpCommand instanceof HelpCommand) && possibleHelpCommand.isValidHelpTrigger(args)) {
                helpList.add(possibleHelpCommand);
            }
        }

        if (helpList.isEmpty()) {
            // unknown command
            MessageHelper.invalidCommand(this.plugin, commandSender);
            return true;
        }

        helpCommand.printHelp(commandSender, args, helpCommand.getHelpJsonMessages(commandSender, helpList));
        return true;
    }

    private boolean tryCommand(List<AbstractCommand> abstractCommands, CommandSender commandSender, String[] args) {
        for (AbstractCommand abstractCommand : abstractCommands) {

            // check if provided arguments are a valid trigger for the subcommands and arguments
            // defined in the command
            if (!abstractCommand.isValidTrigger(args))
                continue;

            if (!abstractCommand.hasPermission(commandSender)) {
                MessageHelper.insufficientPermission(plugin, commandSender);
                return true;
            }

            // If the execution fails with an exception, the manager will search for another command to execute!
            // TODO: really?!
            try {
                abstractCommand.preExecute(
                        commandSender,
                        Arrays.copyOfRange(args, abstractCommand.getSubcommands().size(), args.length));
                return true;

            } catch (CommandException e) {
                MessageHelper.commandExecutionError(this.plugin, commandSender, e);
                e.printStackTrace();
                return true;

            } catch (IllegalCommandArgumentException e) {
                MessageHelper.invalidCommand(this.plugin, commandSender);

                JsonMessage jsonHelp = abstractCommand.getJsonHelp(commandSender);
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
        }

        return false;
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