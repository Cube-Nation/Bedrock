package de.cubenation.bedrock.core.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.argument.Option;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.LengthComparator;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.parts.BedrockJson;
import de.cubenation.bedrock.core.translation.parts.JsonColor;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class CommandPath {

    @Getter
    protected FoundationPlugin plugin;

    @Getter
    protected CommandManager commandManager;

    @Getter
    private final ArrayList<String[]> subcommands = new ArrayList<>();

    @Getter
    private final Command command;

    public CommandPath(FoundationPlugin plugin, CommandManager commandManager, Command command, String[]... subCommands) {
        this.plugin = plugin;
        this.commandManager = commandManager;
        this.subcommands.addAll(Arrays.asList(subCommands));
        this.command = command;
    }

    public boolean tryCommand(BedrockChatSender commandSender, String[] args) {
        // check if provided arguments are a valid trigger for the subcommands and arguments
        // defined in the command
        if (!this.isValidTrigger(args)) {
            return false;
        }


        // If the execution fails with an exception, the manager will search for another command to execute!
        try {
            command.preExecute(
                    commandSender,
                    getStrippedArguments(args)
            );
            return true;

        } catch (CommandException e) {
            plugin.messages().commandExecutionError(commandSender, e);
            e.printStackTrace();
            return true;
        } catch (IllegalCommandArgumentException e) {
            plugin.messages().invalidCommand(commandSender);

            JsonMessage jsonHelp = this.getJsonHelp(commandSender);
            if (jsonHelp == null) {
                plugin.messages().insufficientPermission(commandSender);
            } else {
                jsonHelp.send(commandSender);
            }
            return true;

        } catch (InsufficientPermissionException e) {
            plugin.messages().insufficientPermission(commandSender);
            return true;
        }
    }

    /**
     * Gets auto-completion for argument.
     *
     * @param args   the args of the asking command
     * @param sender the sender of the requested auto-completion.
     * @return the auto-completion for argument
     */
    public List<String> getAutoCompletion(String[] args, BedrockChatSender sender) {
        // not a valid command yet? autocomplete only subcommands.
        if (!isValidTrigger(args)) {
            return getAutoCompletionFromCommands(args);
        }

        return this.command.getAutoCompletion(getStrippedArguments(args), sender);
    }

    private List<String> getAutoCompletionFromCommands(String[] args) {
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

        final ArrayList<String> autoCompletion = new ArrayList<>(Arrays.asList(this.subcommands.get(args.length - 1)));
        autoCompletion.sort(String::compareToIgnoreCase);

        // prioritize largest (because of abbreviations)
        Collections.reverse(autoCompletion);

        // if the user typed a part of the command, return the matching
        String currentArg = args[args.length - 1];
        return autoCompletion.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg.toLowerCase()))
                .collect(Collectors.toList());
    }

    protected boolean isMatchingSubCommands(String[] args) {
        if (args.length < this.getSubcommands().size())
            return false;

        // return true immediately if no subcommands are defined
        if (this.getSubcommands().size() == 0) {
            return true;
        }

        // iterate arguments
        for (int i = 0; i < this.getSubcommands().size(); i++) {
            int finalI = i;
            boolean subCommandMatched = Arrays.stream(this.getSubcommands().get(i)).anyMatch(s -> args[finalI].equalsIgnoreCase(s));
            if (!subCommandMatched) return false;
        }
        return true;
    }

    /**
     * Returns if the subcommand is a valid trigger for the asking command.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    public boolean isValidTrigger(String[] args) {
        return this.isMatchingSubCommands(args);
    }

    public boolean isValidHelpTrigger(String[] args) {
        for (int i = 0; i < args.length && i < getSubcommands().size(); i++) {
            for (String cmd : getSubcommands().get(i)) {
                if (cmd.startsWith(args[i])) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    public String getStringSuggestion() {
        ArrayList<String> suggestString = new ArrayList<>();
        suggestString.add("/" + getCommandManager().getLabel());

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

    public ArrayList<BedrockJson> getColoredSuggestion(@SuppressWarnings("SameParameterValue") Boolean argPlaceholderEnabled) {
        ArrayList<BedrockJson> result = new ArrayList<>();
        String commandHeadline = "/" + getCommandManager().getLabel();
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
            // Process all Arguments
            for (de.cubenation.bedrock.core.command.argument.Argument argument : this.command.getArguments()) {
                // Option Argument
                if (argument instanceof Option option) {
                    result.add(BedrockJson.JsonWithText(option.getKey()).color(JsonColor.SECONDARY));
                    result.add(BedrockJson.Space());
                }

                // Normal Argument
                if (!(argument instanceof Option) || ((Option) argument).hasParameter()) {
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

    /**
     * Define the priority to change the help order
     * Smaller = more important.
     *
     * @return priority
     */
    public Integer getHelpPriority() {
        return 0;
    }

    public boolean displayInHelp() {
        return true;
    }

    public boolean displayInCompletion() {
        return true;
    }

    private String[] getStrippedArguments(String[] args) {
        return Arrays.copyOfRange(args, this.getSubcommands().size(), args.length);
    }
}
