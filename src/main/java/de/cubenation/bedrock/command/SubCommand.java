package de.cubenation.bedrock.command;

import com.google.common.collect.LinkedListMultimap;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import java.util.*;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class SubCommand {

    //region Properties

    private ArrayList<String[]> commands;

    private String[] help;

    private Permission permission;

    //endregion


    //region Constructors


    /**
     * Instantiates a new Sub command.
     *
     * @param command the command
     * @param help the help
     * @param permission the permission
     */
    public SubCommand(final String command, String[] help, Permission permission) {
        init(new ArrayList<String[]>() {{
            add(new String[]{command});
        }}, help, permission);
    }

    /**
     * Instantiates a new Sub command.
     * Each element in commands symbolizes an alias for a command like 'teleport' 'tp'.
     *
     * @param commands the commands
     * @param help the help
     * @param permission the permission
     */
    public SubCommand(final String[] commands, String[] help, Permission permission) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(commands);

        init(list, help, permission);
    }

    /**
     * Instantiates a new Sub command.
     * Each String[]-element in commands symbolizes a new command like 'set' 'bonus'.
     * The String[] contains a single command or aliases like 's' and 'set'.
     *
     * @param commands the commands
     * @param help the help
     * @param permission the permission
     */
    public SubCommand(ArrayList<String[]> commands, String[] help, Permission permission) {
        this.commands = commands;
        this.help = help;
        this.permission = permission;
    }

    private void init(ArrayList<String[]> commands, String[] help, Permission permission) {
        this.commands = commands;
        this.help = help;
        this.permission = permission;
    }

    //endregion


    /**
     * Execute the SubCommand.
     *
     * @param sender the sender
     * @param label the label
     * @param args the args
     * @throws CommandException the command exception
     * @throws CommandException the command exception
     */
    public abstract void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException;


    /**
     * Gets arguments help.
     * The Key describes the Argument. For example {value}.
     * {} - means required.
     * [] - means optional.
     *
     * The Value is the help for the Argument.
     *
     * @return the arguments help
     */
    public abstract LinkedHashMap<String, String> getArguments();

    /**
     * Gets arguments help.
     *
     * @return the arguments help
     */
    public final ArrayList<String> getArgumentsHelp() {
        if (getArguments() != null) {
            ArrayList<String> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : getArguments().entrySet()) {
                list.add(entry.getKey());
            }
            return list;
        }
        return null;
    }


    /**
     * Gets tab completion for argument.
     *
     * @param args the args
     * @return the tab completion list for argument
     */
    @Nullable
    public final String[] getTabCompletionListForArgument(String[] args) {
        System.out.println("Arg.length" + args.length);
        System.out.println(Arrays.toString(args));
        if (commands.size() >= args.length) {
            for (int i = 0; i < args.length; i++) {
                boolean result = false;
                if (!args[i].equals("")) {
                    result = false;
                    for (String com : commands.get(i)) {
                        if (com.startsWith(args[i])) {
                            result = true;
                        }
                    }
                } else {
                    result = true;
                }

                if (!result) {
                    return null;
                }
            }
            return commands.get(args.length - 1);
        }
        return null;
    }


    /**
     * Returns if the subcommand is a valid trigger.
     *
     * @param args the args
     * @return true if it is a valid trigge, else false
     */
    public final boolean isValidTrigger(String[] args) {

        if (args.length >= commands.size()) {
            // Check previous Arguments
            boolean prevResult = true;
            for (int i = 0; i < commands.size(); i++) {
                boolean res = false;
                for (String com : commands.get(i)) {
                    if (args[i].equalsIgnoreCase(com)) {
                        res = true;
                    }
                }
                if (!res) {
                    prevResult = false;
                }
            }
            return prevResult;
        }
        // Not enough arguments
        return false;
    }

    /**
     * Returns if the Sender has permission.
     *
     * @param sender the sender
     * @return true, if the sender has Permissions, else false.
     */
    public final boolean hasPermission(CommandSender sender) {
        if (permission == null) {
            return true;
        } else {
            if (permission.userHasPermission(sender)) return true;
        }
        return false;
    }


    /**
     * Gets commands.
     *
     * @return the commands
     */
    public ArrayList<String[]> getCommands() {
        return commands;
    }

    /**
     * Get help.
     *
     * @return the string [ ]
     */
    public String[] getHelp() {
        return help;
    }

    /**
     * Gets permission.
     *
     * @return the permission
     */
    public Permission getPermission() {
        return permission;
    }

}
