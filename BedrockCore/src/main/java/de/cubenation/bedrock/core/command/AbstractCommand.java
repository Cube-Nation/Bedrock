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
import de.cubenation.bedrock.core.annotation.*;
import de.cubenation.bedrock.core.annotation.condition.AnnotationCondition;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.LengthComparator;
import de.cubenation.bedrock.core.service.command.CommandManager;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.parts.BedrockJson;
import de.cubenation.bedrock.core.translation.parts.JsonColor;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Abstract class for command executor classes
 *
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class AbstractCommand {

    protected FoundationPlugin plugin;

    protected CommandManager commandManager;

    private String description = "";

    private ArrayList<String[]> subcommands = new ArrayList<>();

    private ArrayList<de.cubenation.bedrock.core.command.argument.Argument> arguments = new ArrayList<>();

    private ArrayList<de.cubenation.bedrock.core.authorization.Permission> runtimePermissions = new ArrayList<>();

    private boolean isIngameCommandOnly = false;

    /**
     * The class constructor for all Bedrock commands.
     * <p>
     * Reads all annotations from #execute() and enables features
     * TBD
     *
     * @param plugin         A Bedrock-compatible plugin
     * @param commandManager The Bedrock command manager
     */
    AbstractCommand(FoundationPlugin plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;

        // read annotations from execute methods
        this.parseMethodAnnotations(this.getClass());
    }

    public FoundationPlugin getPlugin() {
        return this.plugin;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    private void parseMethodAnnotations(Class<? extends AbstractCommand> clazz) {
        // Description
        Description description = clazz.getAnnotation(Description.class);
        if (description != null) {
            this.setDescription(description.value());
        }

        // SubCommand/s
        Arrays.stream(clazz.getAnnotationsByType(SubCommand.class)).forEach(
                subCommand -> this.addSubCommand(subCommand.value())
        );

        // Argument/s
        Arrays.stream(clazz.getAnnotationsByType(Argument.class)).forEach(
                this::processArgumentAnnotation
        );

        // Key-Value Argument/s
        Arrays.stream(clazz.getAnnotationsByType(KeyValueArgument.class)).forEach(
                this::processKeyValueArgumentAnnotation
        );

        // Permission/s
        Arrays.stream(clazz.getAnnotationsByType(Permission.class)).forEach(permission -> this.addRuntimePermission(
                this.createPermission(
                        permission.Name(),
                        permission.Role(),
                        permission.RoleName(),
                        permission.Description()
                )
        ));

        // In-game Command only?
        if (clazz.isAnnotationPresent(IngameCommand.class)) {
            this.isIngameCommandOnly = true;
        }
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String[]> getSubcommands() {
        return this.subcommands;
    }

    private void addSubCommand(String[] subCommands) {
        this.subcommands.add(subCommands);
    }

    public ArrayList<de.cubenation.bedrock.core.authorization.Permission> getRuntimePermissions() {
        return this.runtimePermissions;
    }

    private void addRuntimePermission(de.cubenation.bedrock.core.authorization.Permission permission) {
        permission.setPlugin(plugin);
        this.runtimePermissions.add(permission);
    }

    public ArrayList<de.cubenation.bedrock.core.command.argument.Argument> getArguments() {
        return this.arguments;
    }

    private de.cubenation.bedrock.core.authorization.Permission createPermission(String name, Role role, String roleName, String description) {
        de.cubenation.bedrock.core.authorization.Permission permission = new de.cubenation.bedrock.core.authorization.Permission(name);

        if (roleName != null && !roleName.isEmpty()) {
            permission.setRole(de.cubenation.bedrock.core.authorization.Permission.getCommandRole(roleName));
        }

        // CommandRole enums always win (in case a role String is defined)
        if (role != null && !role.equals(Role.NO_ROLE)) {
            permission.setRole(role);
        }

        if (description != null && !description.isEmpty()) {
            permission.setDescriptionLocaleIdent(description);
        }

        return permission;
    }

    private boolean checkArgumentCondition(Class<AnnotationCondition> clazz) {
        try {
            Constructor<AnnotationCondition> constructor = clazz.getConstructor();
            AnnotationCondition condition = constructor.newInstance();
            if (!condition.isValid()) {
                return false;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            plugin.log(Level.SEVERE, "checkArgumentCondition failed", e);
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private void processArgumentAnnotation(Argument commandArgument) {
        if (!this.checkArgumentCondition(commandArgument.Condition())) {
            return;
        }

        de.cubenation.bedrock.core.command.argument.Argument argument = new de.cubenation.bedrock.core.command.argument.Argument(
                this.getPlugin(),
                commandArgument.Description(),
                commandArgument.Placeholder(),
                commandArgument.Optional(),
                null
        );

        if (!commandArgument.Permission().isEmpty()) {
            argument.setPermission(
                    this.createPermission(
                            commandArgument.Permission(),
                            commandArgument.Role(),
                            commandArgument.RoleName(),
                            commandArgument.PermissionDescription()
                    )
            );
        }

        this.addArgument(argument);
    }

    @SuppressWarnings("unchecked")
    private void processKeyValueArgumentAnnotation(KeyValueArgument commandKeyValueArgument) {
        if (!this.checkArgumentCondition(commandKeyValueArgument.Condition())) {
            return;
        }

        de.cubenation.bedrock.core.command.argument.KeyValueArgument keyValueArgument = new de.cubenation.bedrock.core.command.argument.KeyValueArgument(
                this.getPlugin(),
                commandKeyValueArgument.Key(),
                commandKeyValueArgument.Description(),
                commandKeyValueArgument.Placeholder(),
                commandKeyValueArgument.Optional(),
                null
        );

        keyValueArgument.setKeyOnly(commandKeyValueArgument.KeyOnly());

        if (!commandKeyValueArgument.Permission().isEmpty()) {
            keyValueArgument.setPermission(
                    this.createPermission(
                            commandKeyValueArgument.Permission(),
                            commandKeyValueArgument.Role(),
                            commandKeyValueArgument.RoleName(),
                            commandKeyValueArgument.PermissionDescription()
                    )
            );
        }

        this.addArgument(keyValueArgument);
    }

    private void addArgument(de.cubenation.bedrock.core.command.argument.Argument argument) {
        this.arguments.add(argument);
    }

    public void preExecute(BedrockChatSender commandSender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        if (this.isIngameCommandOnly() && !(commandSender instanceof BedrockPlayer)) {
            plugin.messages().mustBePlayer(commandSender);
            return;
        }

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

    private boolean isIngameCommandOnly() {
        return this.isIngameCommandOnly;
    }

    /**
     * Executor for this command class.
     * <p>
     * For a full working command you need to define at least these annotations:
     * <ul>
     * <li>@Description(String)
     * <li>@SubCommand(String[]) (OR)
     * <li>@SubCommands(SubCommands = { @SubCommand(), ... } )
     * </ul>
     * <p>
     * These annotations are optional:
     * <ul>
     * <li>@Permission(Name = "permission.name"[, Role = CommandRole.TYPE]) - optional
     * <li>@Permissions(Permissions = { @Permission() } )
     * <li>@Argument( String Description, String Placeholder, boolean Optional, String Permission, CommandRole Role )
     * <li>@Arguments(Arguments = { @Argument() } )
     * <li>@KeyValueArgument( String Key, String Description, String Placeholder, boolean Optional, String Permission, CommandRole Role, String RoleName )
     * <li>@KeyValueArguments(Arguments = { @KeyValueArgument() } )
     * </ul>
     *
     * @param sender The sender of the command
     * @param args   The list of Arguments
     * @throws CommandException                Thrown by yourself (e.g. when an error occurs)
     * @throws IllegalCommandArgumentException Thrown when the arguments did not match the predefined ones
     * @throws InsufficientPermissionException Thrown when the command issuer does not have enough permissions for this command
     */
    public abstract void execute(BedrockChatSender sender, String[] args)
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
     * @param args   the args of the asking command
     * @param sender the sender of the requested tab completion.
     * @return the tab completion for argument
     */
    public abstract ArrayList<String> getTabCompletion(String[] args, BedrockChatSender sender);

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

    boolean isMatchingSubCommands(String[] args) {
        if (args.length < this.getSubcommands().size())
            return false;

        // return true immediately if no subcommands are defined
        if (this.getSubcommands().size() == 0) {
            return true;
        }

        boolean allSubCommandsMatched = false;
        for (int i = 0; i < this.getSubcommands().size(); i++) {
            boolean subCommandMatched = false;
            for (String subCommand : this.getSubcommands().get(i)) {
                if (args[i].equalsIgnoreCase(subCommand)) {
                    subCommandMatched = true;
                    break;
                }
            }

            allSubCommandsMatched = subCommandMatched;
        }

        return allSubCommandsMatched;
    }


    /**
     * Returns if the subcommand is a valid trigger for the asking command.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    public abstract boolean isValidTrigger(String[] args);

    public boolean isValidHelpTrigger(String[] args) {


        for (int i = 0; i < args.length; i++) {
            for (String cmd : getSubcommands().get(i)) {
                if (cmd.startsWith(args[i])) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    public JsonMessage getJsonHelp(BedrockChatSender sender) {
        return plugin.messages().getHelpForSubCommand(sender, this);
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


    private int getRequiredArgumentsSize() {
        int requiredArguments = 0;
        for (de.cubenation.bedrock.core.command.argument.Argument argument : arguments) {
            if (!argument.isOptional()) {
                requiredArguments++;
            }
        }

        return requiredArguments;
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
            // process all Arguments
            for (de.cubenation.bedrock.core.command.argument.Argument argument : getArguments()) {


                /*
                 * KeyValueArgument
                 *
                 * In case the argument is an instanceof the KeyValueArgument class (which is kind of a
                 * key-value command) we need to prepend the key
                 */
                if (argument instanceof de.cubenation.bedrock.core.command.argument.KeyValueArgument) {
                    de.cubenation.bedrock.core.command.argument.KeyValueArgument keyValueArgument = (de.cubenation.bedrock.core.command.argument.KeyValueArgument) argument;

                    result.add(BedrockJson.JsonWithText(keyValueArgument.getKey()).color(JsonColor.SECONDARY));
                    result.add(BedrockJson.Space());
                }

                /*
                 * Argument placeholder
                 */

                if (!(argument instanceof de.cubenation.bedrock.core.command.argument.KeyValueArgument)
                        || !((de.cubenation.bedrock.core.command.argument.KeyValueArgument) argument).getKeyOnly()) {
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
    public final boolean hasPermission(BedrockChatSender sender) {
        // No permission defined -> sender has permission
        if (getRuntimePermissions().isEmpty())
            return true;

        for (de.cubenation.bedrock.core.authorization.Permission permission : getRuntimePermissions()) {
            if (permission.userHasPermission(sender)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "AbstractCommand{" +
                "description=" + description +
                ", subcommands=" + subcommands +
                ", Arguments=" + arguments +
                ", runtimePermissions=" + runtimePermissions +
                ", plugin=" + plugin +
                ", commandManager=" + commandManager +
                '}';
    }
}

