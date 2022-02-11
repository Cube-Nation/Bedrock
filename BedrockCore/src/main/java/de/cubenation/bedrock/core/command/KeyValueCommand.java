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

package de.cubenation.bedrock.core.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.argument.Argument;
import de.cubenation.bedrock.core.command.argument.Option;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.IgnoreCaseArrayList;
import de.cubenation.bedrock.core.service.command.ComplexCommandManager;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public abstract class KeyValueCommand extends Command {

    @SuppressWarnings("WeakerAccess")
    public KeyValueCommand(FoundationPlugin plugin, ComplexCommandManager commandManager) {
        super(plugin, commandManager);
    }

    @SuppressWarnings("WeakerAccess")
    protected List<Option> getKeyValueArguments() {
        return this.getArguments().stream()
                .filter(argument -> argument instanceof Option)
                .map(argument -> (Option) argument)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("WeakerAccess")
    protected Option getKeyValueArgument(String key) {
        List<Option> options = this.getKeyValueArguments().stream()
                .filter(option -> option.getKey().equals(key)).toList();

        return (options.size() == 1)
                ? options.get(0)
                : null;
    }

    protected boolean hasKeyValueArgument(String key, HashMap<String, String> args) {
        Option argument = this.getKeyValueArgument(key);
        return argument != null && args.keySet().stream().filter(arg -> arg.equals(key)).toList().size() != 0;
    }


    public final void execute(BedrockChatSender sender, String[] args)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        // Parse Arguments

        IgnoreCaseArrayList arrayList = new IgnoreCaseArrayList(Arrays.asList(args));

        HashMap<String, String> parsedArguments = new HashMap<>();

        for (Argument argument : getArguments()) {
            if (argument instanceof Option option) {

                if (arrayList.contains(option.getKey())) {

                    int index = arrayList.indexOf(option.getKey());

                    if (index == -1) {
                        throw new IllegalCommandArgumentException();
                    }

                    if (option.hasParameter()) {
                        index++;
                    }

                    if ((index) >= arrayList.size()) {
                        throw new IllegalCommandArgumentException();
                    }

                    // Add new Key to HasMap
                    parsedArguments.put(option.getKey(), arrayList.get((index)));

                } else {
                    if (!option.isOptional()) {
                        // Missing required Argument
                        throw new IllegalCommandArgumentException();
                    }
                }
            }
        }

        execute(sender, parsedArguments);

    }

    public abstract void execute(BedrockChatSender sender, HashMap<String, String> arguments)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException;

    @Override
    public final List<String> getTabCompletion(String[] args, BedrockChatSender sender) {
        if (this.getSubcommands().size() >= args.length) {
            return getTabCompletionFromCommands(args);
        } else if (args.length > this.getSubcommands().size()) {
            return getTabCompletionFromArguments(args);
        }
        return null;
    }

    private ArrayList<String> getTabCompletionFromArguments(String[] args) {
        if (!checkCommands(args)) {
            return null;
        }

        //Check if last command is equal, not startswith
        if (getSubcommands() != null && !getSubcommands().isEmpty()) {
            int index = this.getSubcommands().size() - 1;
            for (String com : getSubcommands().get(index)) {
                if (com.equalsIgnoreCase(args[index])) {
                    break;
                }
            }
        }

        // TODO
        // Unschön, etwas besseres überlegen
        ArrayList<Option> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof Option) {
                cmdArgs.add((Option) getArguments().get(i));
            }
        }

        ArrayList<Option> recursive = getPossibleArguments(cmdArgs, args, this.getSubcommands().size());

        ArrayList<String> arrayList = new ArrayList<>();
        if (recursive != null) {
            for (Option option : recursive) {
                if (option.getKey().startsWith(args[args.length - 1])) {
                    arrayList.add(option.getKey());
                }
            }
        }

        return arrayList;
    }


    private ArrayList<Option> getPossibleArguments(ArrayList<Option> list, String[] args, int position) {
        if (position >= args.length) {
            return null;
        } else if (position == args.length - 1) {
            return list;
        }

        Option option = containsKey(list, args[position]);
        if (option != null) {
            list.remove(option);
            // Add this option
            position++;
            if (option.hasParameter()) {
                // Add Placeholder size to ignore them
                position++;
            }
            return getPossibleArguments(list, args, position);
        }
        return null;
    }

    private Option containsKey(ArrayList<Option> list, String key) {
        for (Option option : list) {
            if (option.getKey().startsWith(key)) {
                return option;
            }
        }
        return null;
    }


    private boolean checkCommands(String[] args) {

        if (args.length < this.getSubcommands().size()) {
            return false;
        }

        boolean allSubCommandsMatched = false;
        for (int i = 0; i < this.getSubcommands().size(); i++) {
            boolean subCommandMatched = false;

        }

        for (int i = 0; i < this.getSubcommands().size(); i++) {

            boolean validCommand = false;
            for (String com : getSubcommands().get(i)) {
                if (i < this.getSubcommands().size() - 1) {
                    if (com.equalsIgnoreCase(args[i])) {
                        validCommand = true;
                    }
                } else {
                    if (com.startsWith(args[i])) {
                        validCommand = true;
                    }
                }
            }
            if (!validCommand) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns if the subcommand is a valid trigger.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    @Override
    public final boolean isValidTrigger(String[] args) {
        if (!this.isMatchingSubCommands(args))
            return false;

        // check arguments
        int commandArgumentSize = 0;
        for (Argument argument : getArguments()) {
            // Ignore Optional Commands, this can handle the parse method
            if (!argument.isOptional()) {
                // Add 1 for the key
                commandArgumentSize++;
                // Add placeholder Size
                commandArgumentSize++;
            }
        }

        if (args.length < (this.getSubcommands().size() + commandArgumentSize)) {
            return false;
        }

        // Unschön, etwas besseres überlegen
        ArrayList<Option> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof Option) {
                cmdArgs.add((Option) getArguments().get(i));
            }
        }

        for (int i = this.getSubcommands().size(); i < args.length; i++) {
            Option argument = containsKey(cmdArgs, args[i]);
            if (argument != null) {
                cmdArgs.remove(argument);
            }
        }

        // cmdArgs should contains none or optional commands
        // else return false
        for (Option argument : cmdArgs) {
            if (!argument.isOptional()) {
                return false;
            }
        }

        return true;
    }

}

