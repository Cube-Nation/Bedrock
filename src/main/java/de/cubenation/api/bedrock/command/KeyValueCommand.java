package de.cubenation.api.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.argument.KeyValueArgument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.IgnoreCaseArrayList;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 */
@SuppressWarnings("unused")
public abstract class KeyValueCommand extends AbstractCommand {

    public KeyValueCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
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

                if (arrayList.contains(keyValueArgument.getRuntimeKey())) {

                    int index = arrayList.indexOf(keyValueArgument.getRuntimeKey());

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
        if (this.subcommands.size() >= args.length) {
            return getTabCompletionFromCommands(args);
        } else if (args.length > this.subcommands.size()) {
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
            int index = this.subcommands.size() - 1;
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

        ArrayList<KeyValueArgument> recursive = getPossibleArguments(cmdArgs, args, this.subcommands.size());

        ArrayList<String> arrayList = new ArrayList<>();
        if (recursive != null) {
            for (KeyValueArgument argument : recursive) {
                if (argument.getRuntimeKey().startsWith(args[args.length - 1])) {
                    arrayList.add(argument.getRuntimeKey());
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
            if (argument.getRuntimeKey().startsWith(key)) {
                return argument;
            }
        }
        return null;
    }


    private boolean checkCommands(String[] args) {

        if (args.length < this.subcommands.size()) {
            return false;
        }

        for (int i = 0; i < this.subcommands.size(); i++) {

            boolean validCommand = false;
            for (String com : getSubcommands().get(i)) {
                if (i < this.subcommands.size() - 1) {
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
        if (!checkCommands(args)) {
            return false;
        }

        if (!checkArguments(args)) {
            return false;
        }

        return true;
    }

    private boolean checkArguments(String[] args) {

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

        if (args.length < (this.subcommands.size() + commandArgumentSize)) {
            return false;
        }

        // Unschön, etwas besseres überlegen
        ArrayList<KeyValueArgument> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof KeyValueArgument) {
                cmdArgs.add((KeyValueArgument) getArguments().get(i));
            }
        }

        for (int i = this.subcommands.size(); i < args.length; i++) {
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