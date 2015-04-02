package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class BedrockSubCommand {

    //region Properties
    private String name;

    private String[] aliases;

    private String permission;

    private List<String> help;

    private CommandExecutorType commandExecutorType = CommandExecutorType.PLAYER;
    //endregion


    //region Constructors


    /**
     * Instantiates a new Bedrock SubCommand.
     *
     * @param name the name
     * @param help the help
     */
    public BedrockSubCommand(String name, List<String> help) {
        this.name = name;
        this.help = help;
    }

    /**
     * Instantiates a new Bedrock SubCommand.
     *
     * @param name the name
     * @param aliases the aliases
     * @param help the help
     */
    public BedrockSubCommand(String name, String[] aliases, List<String> help) {
        this.name = name;
        this.aliases = aliases;
        this.help = help;
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
    public abstract void execute(CommandSender sender, String label, String[] args) throws CommandException;


    public abstract int getMinimumArguments();

    public abstract String getArgumentsHelp();


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
        if (permission == null) return true;
        return sender.hasPermission(permission);
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
    public String getPermission() {
        return permission;
    }

    /**
     * Gets help.
     *
     * @return the help
     */
    public List<String> getHelp() {
        return help;
    }

    /**
     * Gets the type of the command executor.
     *
     * @return the type of the command executor
     */
    public CommandExecutorType getCommandExecutorType() {
        return commandExecutorType;
    }

    /**
     * Sets the type of the command executor.
     *
     * @param commandExecutorType the command executor
     */
    public void setCommandExecutorType(CommandExecutorType commandExecutorType) {
        this.commandExecutorType = commandExecutorType;
    }

    public enum CommandExecutorType {
        PLAYER,
        COMMANDLINE
    }
}
