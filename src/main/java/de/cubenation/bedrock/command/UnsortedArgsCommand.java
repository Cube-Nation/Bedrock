package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.argument.UnsortedArgument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.IgnoreCaseArrayList;
import de.cubenation.bedrock.helper.MessageHelper;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
@SuppressWarnings("unused")
public abstract class UnsortedArgsCommand extends AbstractCommand {

    public UnsortedArgsCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public final void execute(CommandSender sender, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        // Parse Arguments

        IgnoreCaseArrayList arrayList = new IgnoreCaseArrayList(Arrays.asList(args));

        HashMap<String, String> parsedArguments = new HashMap<>();

        for (Argument argument : getArguments()) {
            if (argument instanceof UnsortedArgument) {
                UnsortedArgument unsortedArgument = (UnsortedArgument) argument;

                if (arrayList.contains(unsortedArgument.getKey())) {

                    int index = arrayList.indexOf(unsortedArgument.getKey());
                    // Add new Key to HasMap

                    parsedArguments.put(unsortedArgument.getKey(), unsortedArgument.getRuntimePlaceholder());



                } else {
                    if (!unsortedArgument.isOptional()) {
                        // Missing required Argument
                        throw new IllegalCommandArgumentException();
                    }
                }
            }
        }

        execute(sender, subcommands, parsedArguments);

    }

    public abstract void execute(CommandSender sender, String[] subcommands, HashMap<String, String> arguments) throws CommandException, IllegalCommandArgumentException;

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
        int index = this.subcommands.size() - 1;
        for (String com : getSubcommands().get(index)) {
            if (!com.equalsIgnoreCase(args[index])) {
                return null;

            }
        }

        // TODO
        // Unschön, etwas besseres überlegen
        ArrayList<UnsortedArgument> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof UnsortedArgument) {
                cmdArgs.add((UnsortedArgument) getArguments().get(i));
            }
        }

        ArrayList<UnsortedArgument> recursive = getPossibleArguments(cmdArgs, args, this.subcommands.size());
        
        ArrayList<String> arrayList = new ArrayList<>();
        if (recursive != null) {
            for (UnsortedArgument argument : recursive) {
                if (argument.getKey().startsWith(args[args.length - 1])) {
                    arrayList.add(argument.getKey());
                }
            }
        }

        return arrayList;
    }


    private ArrayList<UnsortedArgument> getPossibleArguments(ArrayList<UnsortedArgument> list, String[] args, int position) {
        if (position >= args.length) {
            return null;
        } else if (position == args.length - 1) {
            return list;
        }

        UnsortedArgument argument = containsKey(list, args[position]);
        if (argument != null) {
            list.remove(argument);
            // Add this argument
            position++;
            // Add Placeholder size to ignore them
            position ++;
            return getPossibleArguments(list, args, position);
        }
        return null;
    }

    private UnsortedArgument containsKey(ArrayList<UnsortedArgument> list, String key) {
        for (UnsortedArgument argument : list) {
            if (argument.getKey().startsWith(key)) {
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
            System.out.println("!checkCommands");
            return false;
        }

        if (!checkArguments(args)) {
            System.out.println("!checkArguments");
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
        ArrayList<UnsortedArgument> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); i++) {
            if (getArguments().get(i) instanceof UnsortedArgument) {
                cmdArgs.add((UnsortedArgument) getArguments().get(i));
            }
        }

        System.out.println("All Args");
        System.out.println(cmdArgs);

        for (int i = this.subcommands.size(); i < args.length; i++) {
            UnsortedArgument argument = containsKey(cmdArgs, args[i]);
            if (argument != null) {
                cmdArgs.remove(argument);
            }
        }

        // cmdArgs should contains none or optional commands
        // else return false
        System.out.println("Should contain only optional");
        System.out.println(cmdArgs);
        for (UnsortedArgument argument : cmdArgs) {
            if (!argument.isOptional()) {
                return false;
            }
        }

        return true;
    }

    public TextComponent getBeautifulHelp(CommandSender sender) {
        return MessageHelper.getHelpForSubCommand(plugin, sender, this);
    }

}
