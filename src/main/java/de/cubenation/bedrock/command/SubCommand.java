package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.permission.Permission;
import org.bukkit.command.CommandSender;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class SubCommand {

    //region Properties
    private String name;

    private String[] aliases;

    private Permission permission;

    private String[] help;

    //endregion

    //region Constructors
    /**
     * Instantiates a new Bedrock SubCommand.
     *
     * @param name the name
     * @param help the help
     */
    public SubCommand(String name, String[] help, Permission permission) {
        this.name = name;
        this.help = help;
        this.permission = permission;
    }

    /**
     * Instantiates a new Bedrock SubCommand.
     *
     * @param name    the name
     * @param aliases the aliases
     * @param help    the help
     */
    public SubCommand(String name, String[] aliases, String[] help, Permission permission) {
        this.name = name;
        this.aliases = aliases;
        this.help = help;
        this.permission = permission;
    }
    //endregion


    /**
     * Execute the SubCommand.
     *
     * @param sender the sender
     * @param label  the label
     * @param args   the args
     * @throws CommandException the command exception
     */
    public abstract void execute(CommandSender sender, String label, String[] args) throws CommandException, IllegalCommandArgumentException;

    public abstract int getMinimumArguments();

    public abstract String getArgumentsHelp();

    public abstract boolean isPlayerCommand();

    /**
     * Get tab completion list for argument.
     * Argument 0 ist the first.
     *
     * @param argument the argument
     * @return the completionString.
     */
    public abstract String[] getTabCompletionListForArgument(int argument);


    /**
     * Returns if the SubCommand is a valid trigger.
     *
     * @param name the name
     * @return the boolean
     */
    public final boolean isValidTrigger(String name) {
        if (this.name.equalsIgnoreCase(name)) {
            return true;
        }
        if (aliases != null) {
            for (String alias : aliases) {
                if (alias.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
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
            System.out.println("Permission == null");
            return true;
        } else {
            if (permission.userHasPermission(sender)) return true;
            if (permission.getRole() != null) {
                if (permission.getRole().userHasRole(sender)) return true;
            }
        }
        return false;
    }


    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get aliases.
     *
     * @return the aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Gets permission.
     *
     * @return the permission
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * Gets help.
     *
     * @return the help
     */
    public String[] getHelp() {
        return help;
    }

}
