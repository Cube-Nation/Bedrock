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

package de.cubenation.api.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.argument.KeyValueArgument;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.IgnoreCaseArrayList;
import de.cubenation.api.bedrock.service.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public abstract class KeyValueCommand extends AbstractCommand {

    @SuppressWarnings("WeakerAccess")
    public KeyValueCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @SuppressWarnings("WeakerAccess")
    protected List<KeyValueArgument> getKeyValueArguments() {
        return this.getArguments().stream()
                .filter(argument -> argument instanceof KeyValueArgument)
                .map(argument -> (KeyValueArgument) argument)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("WeakerAccess")
    protected KeyValueArgument getKeyValueArgument(String key) {
        List<KeyValueArgument> keyValueArguments = this.getKeyValueArguments().stream()
                .filter(keyValueArgument -> keyValueArgument.getKey().equals(key))
                .collect(Collectors.toList());

        return (keyValueArguments.size() == 1)
                ? keyValueArguments.get(0)
                : null;
    }

    protected boolean hasKeyValueArgument(String key, HashMap<String, String> args) {
        KeyValueArgument argument = this.getKeyValueArgument(key);
        return argument != null && args.keySet().stream().filter(arg -> arg.equals(key)).collect(Collectors.toList()).size() != 0;
    }


    @Override
    public final void execute(CommandSender sender, String[] args)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        // Parse Arguments

        IgnoreCaseArrayList arrayList = new IgnoreCaseArrayList(Arrays.asList(args));

        HashMap<String, String> parsedArguments = new HashMap<>();

        for (Argument argument : getArguments()) {
            if (argument instanceof KeyValueArgument) {

                KeyValueArgument keyValueArgument = (KeyValueArgument) argument;

                if (arrayList.contains(keyValueArgument.getKey())) {

                    int index = arrayList.indexOf(keyValueArgument.getKey());

                    if (index == -1) {
                        throw new IllegalCommandArgumentException();
                    }

                    if (!keyValueArgument.getKeyOnly()) {
                        index++;
                    }

                    if ((index) >= arrayList.size()) {
                        throw new IllegalCommandArgumentException();
                    }

                    // Add new Key to HasMap
                    parsedArguments.put(keyValueArgument.getKey(), arrayList.get((index)));

                } else {
                    if (!keyValueArgument.isOptional()) {
                        // Missing required Argument
                        throw new IllegalCommandArgumentException();
                    }
                }
            }
        }

        execute(sender, parsedArguments);

    }

    public abstract void execute(CommandSender sender, HashMap<String, String> arguments)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException;

    @Override
    public final ArrayList<String> getTabCompletion(String[] args, CommandSender sender) {
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
        ArrayList<KeyValueArgument> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof KeyValueArgument) {
                cmdArgs.add((KeyValueArgument) getArguments().get(i));
            }
        }

        ArrayList<KeyValueArgument> recursive = getPossibleArguments(cmdArgs, args, this.getSubcommands().size());

        ArrayList<String> arrayList = new ArrayList<>();
        if (recursive != null) {
            for (KeyValueArgument argument : recursive) {
                if (argument.getKey().startsWith(args[args.length - 1])) {
                    arrayList.add(argument.getKey());
                }
            }
        }

        return arrayList;
    }


    private ArrayList<KeyValueArgument> getPossibleArguments(ArrayList<KeyValueArgument> list, String[] args, int position) {
        if (position >= args.length) {
            return null;
        } else if (position == args.length - 1) {
            return list;
        }

        KeyValueArgument argument = containsKey(list, args[position]);
        if (argument != null) {
            list.remove(argument);
            // Add this argument
            position++;
            if (!argument.getKeyOnly()) {
                // Add Placeholder size to ignore them
                position++;
            }
            return getPossibleArguments(list, args, position);
        }
        return null;
    }

    private KeyValueArgument containsKey(ArrayList<KeyValueArgument> list, String key) {
        for (KeyValueArgument argument : list) {
            if (argument.getKey().startsWith(key)) {
                return argument;
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
        ArrayList<KeyValueArgument> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof KeyValueArgument) {
                cmdArgs.add((KeyValueArgument) getArguments().get(i));
            }
        }

        for (int i = this.getSubcommands().size(); i < args.length; i++) {
            KeyValueArgument argument = containsKey(cmdArgs, args[i]);
            if (argument != null) {
                cmdArgs.remove(argument);
            }
        }

        // cmdArgs should contains none or optional commands
        // else return false
        for (KeyValueArgument argument : cmdArgs) {
            if (!argument.isOptional()) {
                return false;
            }
        }

        return true;
    }

}
