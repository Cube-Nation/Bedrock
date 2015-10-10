package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.permission.Permission;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class AbstractCommand {

    protected StringBuilder description = new StringBuilder();

    //protected ArrayList<String> permissions = new ArrayList<>();

    protected ArrayList<String[]> subcommands = new ArrayList<>();

    protected ArrayList<Argument> arguments = new ArrayList<>();

    private ArrayList<Permission> runtimePermissions = new ArrayList<>();

    protected BasePlugin plugin;

    protected CommandManager commandManager;
    
    
    public AbstractCommand(BasePlugin plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;

        setDescription(this.description);
        setSubCommands(this.subcommands);
        setArguments(this.arguments);

        for (Argument argument : this.arguments) {
            argument.setPlugin(plugin);
        }

        ArrayList<String> permissions = new ArrayList<>();
        setPermissions(permissions);

        for (String permission : permissions) {
            addRuntimePermission(new Permission(permission, getPlugin()));
        }
    }

    /**
     *
     * @param permissions   ArrayList of permission strings
     */
    public abstract void setPermissions(ArrayList<String> permissions);

    /**
     *
     * @param subcommands    ArrayList with subcommand string arrays
     */
    public abstract void setSubCommands(ArrayList<String[]> subcommands);

    /**
     *
     * @param description   Locale identifier string
     */
    public abstract void setDescription(StringBuilder description);

    /**
     *
     * @param arguments     ArrayList of de.cubenation.bedrock.command.argument.Argument objects
     */
    public abstract void setArguments(ArrayList<Argument> arguments);

    /**
     * @param sender      the sender of the command
     * @param subcommands the list of subcommands
     * @param args        the list of arguments
     * @throws CommandException
     * @throws IllegalCommandArgumentException
     */
    public abstract void execute(CommandSender sender,
                                 String[] subcommands,
                                 String[] args) throws CommandException, IllegalCommandArgumentException;

    /**
     * Gets tab completion for argument.
     *
     * @param args the args of the asking command
     * @return the tab completion for argument
     */
    public abstract ArrayList<String> getTabCompletion(String[] args, CommandSender sender);

    public final ArrayList<String> getTabCompletionFromCommands(String[] args) {
        if (this.subcommands.size() < args.length) {
            return null;
        }

        for (int i = 0; i < args.length; i++) {

            boolean validCommand = false;
            for (String com : this.subcommands.get(i)) {
                // Last Argument can start with
                // Other MUST be equal
                if (i < args.length - 1) {
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
                return null;
            }
        }

        final ArrayList<String> list = new ArrayList<>(Arrays.asList(this.subcommands.get(args.length - 1)));

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareToIgnoreCase(s1);
            }
        });

        // Just return the "largets" command for completion to help the user to choose the right.
        final String completionCommand = list.get(0);

        return new ArrayList<String>() {{
            add(completionCommand);
        }};
    }

    /**
     * Returns if the subcommand is a valid trigger for the asking command.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    public abstract boolean isValidTrigger(String[] args);

    public abstract TextComponent getBeautifulHelp(CommandSender sender);

    public boolean displayInHelp() {
        return true;
    }

    public boolean displayInCompletion() {
        return true;
    }

    /**
     * Returns if the Sender has permission.
     *
     * @param sender the sender
     * @return true, if the sender has Permissions, else false.
     */
    public final boolean hasPermission(CommandSender sender) {
        for (Permission permission : getRuntimePermissions()) {
            if (permission.userHasPermission(sender)) {
                return true;
            }
        }
        return false;
    }

    protected void addRuntimePermission(Permission permission) {
        this.runtimePermissions.add(permission);
    }

    public ArrayList<Permission> getRuntimePermissions() {
        return this.runtimePermissions;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public BasePlugin getPlugin() {
        return this.plugin;
    }

    public ArrayList<String[]> getSubcommands() {
        return subcommands;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public String getDescription() {
        return description.toString();
    }

    @Override
    public String toString() {
        return "AbstractCommand{" +
                "description=" + description +
                ", subcommands=" + subcommands +
                ", arguments=" + arguments +
                ", runtimePermissions=" + runtimePermissions +
                ", plugin=" + plugin +
                ", commandManager=" + commandManager +
                '}';
    }
}
