package de.cubenation.api.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.argument.KeyValueArgument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.LengthComparator;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.translation.JsonMessage;
import de.cubenation.api.bedrock.translation.parts.BedrockJson;
import de.cubenation.api.bedrock.translation.parts.JsonColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 */
public abstract class AbstractCommand {

    protected StringBuilder description = new StringBuilder();

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

        setPermissions(this.runtimePermissions);
        for (Permission permission : this.runtimePermissions) {
            permission.setPlugin(plugin);
        }

        setArguments(this.arguments);
        for (Argument argument : this.arguments) {
            argument.setPlugin(plugin);
        }
    }

    /**
     * @param permissions ArrayList of permission strings
     */
    public abstract void setPermissions(ArrayList<Permission> permissions);

    /**
     * @param subcommands ArrayList with subcommand string arrays
     */
    public abstract void setSubCommands(ArrayList<String[]> subcommands);

    /**
     * @param description Locale identifier string
     */
    public abstract void setDescription(StringBuilder description);

    /**
     * @param arguments ArrayList of Argument objects
     */
    public abstract void setArguments(ArrayList<Argument> arguments);

    public void preExecute(CommandSender commandSender, String[] args)  throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        if (performPreArgumentCheck()) {
            int requiredArgumentsSize = getRequiredArgumentsSize();
            if (requiredArgumentsSize > 0) {
                if (args == null || args.length < requiredArgumentsSize) {
                    throw new IllegalCommandArgumentException();
                }
            }
        }

        execute(commandSender, args);
    }

    /**
     * @param sender      the sender of the command
     * @param args        the list of arguments
     * @throws CommandException
     * @throws IllegalCommandArgumentException
     */
    public abstract void execute(CommandSender sender, String[] args)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException;

    /**
     * Define the priority to change the help order
     * Smaller = more important.
     *
     * @return priority
     */
    public Integer getHelpPriority() {
        return 0;
    }

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
        list.sort(String::compareToIgnoreCase);

        // If the user typed a part of the command, return the matching
        String completionCommand = "";

        for (String completion : list) {
            if (completion.startsWith(args[args.length - 1])) {
                completionCommand = completion;
                break;
            }
        }

        // If the user typed nothing, return the largest
        if (completionCommand.equals("")) {
            completionCommand = list.get(list.size() - 1);
        }

        final String finalCompletionCommand = completionCommand;
        return new ArrayList<String>() {{
            add(finalCompletionCommand);
        }};
    }

    /**
     * Returns if the subcommand is a valid trigger for the asking command.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    public abstract boolean isValidTrigger(String[] args);

    public JsonMessage getJsonHelp(CommandSender sender) {
        return MessageHelper.getHelpForSubCommand(plugin, sender, this);
    }

    public String getStringSuggestion() {

        ArrayList<String> suggestString = new ArrayList<>();
        suggestString.add("/" + getCommandManager().getPluginCommand().getLabel());

        if (getSubcommands() != null) {
            for (String[] commands : getSubcommands()) {
                // sort commands by length
                Arrays.sort(commands, new LengthComparator());

                // add longest command to suggest string
                suggestString.add(commands[commands.length - 1]);
            }
        }

        return StringUtils.join(suggestString, " ");
    }


    private int getRequiredArgumentsSize() {
        int requiredArguments = 0;
        for (Argument argument : arguments) {
            if (!argument.isOptional()) {
                requiredArguments++;
            }
        }

        return requiredArguments;
    }

    public ArrayList<BedrockJson> getColoredSuggestion(Boolean argPlaceholderEnabled) {
        ArrayList<BedrockJson> result = new ArrayList<>();
        String commandHeadline = "/" + getCommandManager().getPluginCommand().getLabel();
        result.add(BedrockJson.JsonWithText(commandHeadline).color(JsonColor.PRIMARY));
        result.add(BedrockJson.Space());

        if (getSubcommands() != null) {
            for (String[] commands : getSubcommands()) {
                // sort commands by length
                Arrays.sort(commands, new LengthComparator());

                String subCmd = commands[commands.length - 1];
                BedrockJson subCommand = BedrockJson.JsonWithText(subCmd).color(JsonColor.SECONDARY);

                result.add(subCommand);
                result.add(BedrockJson.Space());
            }
        }

        if (argPlaceholderEnabled) {
            // process all arguments
            for (Argument argument : getArguments()) {


                /*
                 * KeyValueArgument
                 *
                 * In case the argument is an instanceof the KeyValueArgument class (which is kind of a
                 * key-value command) we need to prepend the key
                 */
                if (argument instanceof KeyValueArgument) {
                    KeyValueArgument keyValueArgument = (KeyValueArgument) argument;

                    result.add(BedrockJson.JsonWithText(keyValueArgument.getRuntimeKey()).color(JsonColor.SECONDARY));
                    result.add(BedrockJson.Space());
                }

                /*
                 * Argument placeholder
                 */

                if (!(argument instanceof KeyValueArgument)
                        || !((KeyValueArgument) argument).getKeyOnly()) {
                    BedrockJson runtimePlaceholder = BedrockJson.JsonWithText(argument.getRuntimePlaceholder())
                            .color(JsonColor.GRAY)
                            .italic(argument.isOptional());

                    result.add(runtimePlaceholder);
                    result.add(BedrockJson.Space());
                }
            }
        }

        return result;
    }

    public boolean displayInHelp() {
        return true;
    }

    public boolean displayInCompletion() {
        return true;
    }

    public boolean performPreArgumentCheck() {
        return true;
    }

    /**
     * Returns if the Sender has permission.
     *
     * @param sender the sender
     * @return true, if the sender has Permissions, else false.
     */
    public final boolean hasPermission(CommandSender sender) {
        if (getRuntimePermissions().isEmpty()) {
            // No Permission defined -> sender has permission
            return true;
        }

        for (Permission permission : getRuntimePermissions()) {
            if (permission.userHasPermission(sender)) {
                return true;
            }
        }
        return false;
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