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

package de.cubenation.bedrock.core.service.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.command.predefined.HelpCommand;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public abstract class ComplexCommandManager implements CommandManager {

    private FoundationPlugin plugin;

    private List<Command> commands = new ArrayList<>();

    private HelpCommand helpCommand;

    private String label;


    public ComplexCommandManager(FoundationPlugin plugin, String label) {
        this.plugin = plugin;
        this.label = label;

        this.helpCommand = new HelpCommand(plugin, this);
        this.commands.add(helpCommand);
    }

    public void addCommand(Command command) {
        this.commands.add(command);
    }


    @Override
    public boolean onCommand(BedrockChatSender commandSender, String[] args) {
        if (args.length == 0) {
            try {
                helpCommand.preExecute(commandSender, args);
            } catch (Exception e) {
                plugin.log(Level.INFO, "Error while executing help command. Shouldn't happen!");
                e.printStackTrace();
            }
            return true;
        } else {

            // Try commands with subcommands
            if (this.tryCommand(
                    this.commands.stream()
                            .filter(abstractCommand -> abstractCommand.getSubcommands().size() != 0)
                            .collect(Collectors.toList()),
                    commandSender,
                    args
            )) {
                return true;
            }

            // TODO: Is this ever used?
            // Try commands without subcommands
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

        // Display help
        ArrayList<Command> helpList = new ArrayList<>();
        for (Command possibleHelpCommand : helpCommand.getHelpCommands()) {
            if (!(possibleHelpCommand instanceof HelpCommand) && possibleHelpCommand.isValidHelpTrigger(args)) {
                helpList.add(possibleHelpCommand);
            }
        }

        if (helpList.isEmpty()) {
            // Unknown command
            plugin.messages().invalidCommand(commandSender);
            return true;
        }

        helpCommand.printHelp(commandSender, args, helpCommand.getHelpJsonMessages(commandSender, helpList));
        return true;
    }

    private boolean tryCommand(List<Command> commands, BedrockChatSender commandSender, String[] args) {
        for (Command command : commands) {
            if(command.tryCommand(commandSender, args))
                return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (Command cmd : getCompletionCommands()) {
            if (!cmd.hasPermission(sender)) {
                continue;
            }

            List tabCom = cmd.getTabCompletion(args, sender);
            if (tabCom != null) {
                list.addAll(tabCom);
            }
        }
        // Remove duplicates
        Set<String> set = new HashSet<>(list);
        if (set.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(set);
        }
    }

    public List<Command> getCompletionCommands() {
        ArrayList<Command> completionCommands = new ArrayList<>();
        for (Command command : getCommands()) {
            if (command.displayInCompletion()) {
                completionCommands.add(command);
            }
        }
        return completionCommands;
    }

    //region Getter
    public FoundationPlugin getPlugin() {
        return plugin;
    }

    public String getLabel() {
        return label;
    }

    public List<Command> getCommands() {
        return commands;
    }

    //endregion
}
