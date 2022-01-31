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
import de.cubenation.bedrock.core.command.argument.Argument;
import de.cubenation.bedrock.core.command.argument.type.ArgumentType;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.LengthComparator;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.parts.BedrockJson;
import de.cubenation.bedrock.core.translation.parts.JsonColor;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Abstract class for command executor classes
 *
 * @author Cube-Nation
 * @version 2.0
 */

@ToString
public abstract class AbstractCommand {

    protected FoundationPlugin plugin;

    protected CommandManager commandManager;

    private String description = "";

    private ArrayList<String[]> subcommands = new ArrayList<>();

    private ArrayList<de.cubenation.bedrock.core.command.argument.Argument> arguments = new ArrayList<>();

    private ArrayList<de.cubenation.bedrock.core.authorization.Permission> runtimePermissions = new ArrayList<>();

    private boolean isIngameCommandOnly = false;

    private Method executeMethod;

    /**
     * The class constructor for all Bedrock commands.
     * <p>
     * Reads all annotations from #execute() and enables features
     * TBD
     *
     * @param plugin         A Bedrock-compatible plugin
     * @param commandManager The Bedrock command manager
     */
    public AbstractCommand(FoundationPlugin plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;

        // read annotations from execute methods
        try {
            this.parseMethodAnnotations(this.getClass());
            this.parseExecuteMethod();
        } catch (CommandInitException e) {
            e.printStackTrace();
        }
    }

    public FoundationPlugin getPlugin() {
        return this.plugin;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    private void parseMethodAnnotations(Class<? extends AbstractCommand> clazz) throws CommandInitException {
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
        // TODO: Do something about the localisation stuff
//        Arrays.stream(clazz.getAnnotationsByType(Argument.class)).forEach(
//                this::processArgumentAnnotation
//        );

        // Key-Value Argument/s
        for (KeyValueArgument keyValueArgument : clazz.getAnnotationsByType(KeyValueArgument.class)) {
            processKeyValueArgumentAnnotation(keyValueArgument);
        }

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

    public void parseExecuteMethod() throws CommandInitException {
        // get possible execute method matches
        List<Method> matches = Arrays.stream(getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("execute"))
                .collect(Collectors.toList());

        // only proceed if there's exactly one execute method
        if (matches.isEmpty()) {
            throw new CommandInitException(getClass().getName()+": Execute method not found. Please contact plugin author.");
        } else if (matches.size() > 1) {
            throw new CommandInitException(getClass().getName()+": Multiple execute methods found. Please contact plugin author.");
        }
        this.executeMethod = matches.get(0);

        // get method parameters
        Parameter[] executeParameters = this.executeMethod.getParameters();
        // check for bedrockChatSender
        if (executeParameters.length == 0 || !executeParameters[0].getType().equals(BedrockChatSender.class)) {
            throw new CommandInitException(getClass().getName()+": Execute method needs to start with BedrockChatSender parameter. Please contact plugin author.");
        }
        // remove BedrockChatSender from parameters
        executeParameters = Arrays.copyOfRange(executeParameters, 1, executeParameters.length);

        // create argument objects
        for (Parameter executeParameter : executeParameters) {
            processExecuteParameter(executeParameter);
        }

        // check if optional arguments are at the end
        boolean followsOptional = false;
        for (Argument argument : this.arguments) {
            if (argument.isOptional()) {
                followsOptional = true;
            } else if (followsOptional) {
                throw new CommandInitException(getClass().getName()+": Invalid execute method. Optional parameters need to be last.");
            }
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

    private void processExecuteParameter(Parameter parameter) throws CommandInitException {
        // TODO: argument conditions
        // if (!this.checkArgumentCondition(commandArgument.Condition())) {
        //    return;
        // }

        Class<?> clazz = parameter.getType();
        boolean optional = clazz.equals(Optional.class);
        if (optional) {
            clazz = ((Class) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0]);
        }
        boolean array = clazz.isArray();
        if (array) {
            clazz = clazz.getComponentType();
        }

        // TODO: argument localisation
        Argument argument = new Argument(
                this.getPlugin(),
                "command.bedrock.key.desc",
                "command.bedrock.key.ph",
                optional,
                array,
                null,
                clazz
        );

        // TODO: argument permissions
        //if (!commandArgument.Permission().isEmpty()) {
        //    argument.setPermission(
        //            this.createPermission(
        //                    commandArgument.Permission(),
        //                    commandArgument.Role(),
        //                    commandArgument.RoleName(),
        //                    commandArgument.PermissionDescription()
        //            )
        //    );
        //}

        this.addArgument(argument);
    }

    @SuppressWarnings("unchecked")
    private void processKeyValueArgumentAnnotation(KeyValueArgument commandKeyValueArgument) throws CommandInitException {
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

        ArrayList<Object> executeParameterValues = tryCastInputToArgumentTypes(commandSender, args);
        if (executeParameterValues == null) {
            return;
        }
        executeParameterValues.add(0, commandSender);

        try {
            Object[] params = executeParameterValues.toArray();
            this.executeMethod.invoke(this, params);
        } catch (IllegalAccessException e) {
            throw new CommandException(getClass().getName()+": Execute method not accessible.");
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
    }

    public boolean tryCommand(BedrockChatSender commandSender, String[] args) {
        // check if provided arguments are a valid trigger for the subcommands and arguments
        // defined in the command
        if (!this.isValidTrigger(args))
            return false;

        if (!this.hasPermission(commandSender)) {
            plugin.messages().insufficientPermission(commandSender);
            return true;
        }

        // If the execution fails with an exception, the manager will search for another command to execute!
        try {
            this.preExecute(
                    commandSender,
                    Arrays.copyOfRange(args, this.getSubcommands().size(), args.length));
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

    private boolean isIngameCommandOnly() {
        return this.isIngameCommandOnly;
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

    /**
     * Gets tab completion for argument.
     *
     * @param args   the args of the asking command
     * @param sender the sender of the requested tab completion.
     * @return the tab completion for argument
     */
    public abstract List<String> getTabCompletion(String[] args, BedrockChatSender sender);

    public final List<String> getTabCompletionFromCommands(String[] args) {
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

        final ArrayList<String> tabCompletion = new ArrayList<>(Arrays.asList(this.subcommands.get(args.length - 1)));
        tabCompletion.sort(String::compareToIgnoreCase);

        // prioritize largest (because of abbreviations)
        Collections.reverse(tabCompletion);

        // if the user typed a part of the command, return the matching
        String currentArg = args[args.length - 1];
        return tabCompletion.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg.toLowerCase()))
                .collect(Collectors.toList());
    }

    public final List<String> getTabCompletionFromArguments(BedrockChatSender sender, String[] args) {
        int argIndex = args.length - this.getSubcommands().size() - 1;
        if (argIndex < 0 || argIndex >= this.arguments.size()) {
            return null;
        }
        String[] realArgs = Arrays.copyOfRange(args, this.getSubcommands().size(), args.length);

        Argument argument = this.arguments.get(argIndex);

        ArgumentType<?> argumentType = argument.getArgumentType();
        if (argumentType == null) {
            return null;
        }
        Iterable<String> tabCompletionFromArguments = argumentType.onAutoComplete(sender, realArgs);
        if (tabCompletionFromArguments == null) {
            return null;
        }

        ArrayList<String> tabCompletion = new ArrayList<>();
        tabCompletionFromArguments.forEach(tabCompletion::add);

        // If the user typed a part of the command, return the matching
        String currentArg = args[args.length - 1];
        return tabCompletion.stream()
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
     * Returns an ArrayList of casted objects matching the types-array or throws exception for invalid types.
     *
     * @param args the value of the arguments
     * @return ArrayList of casted objects
     */
    protected ArrayList<Object> tryCastInputToArgumentTypes(BedrockChatSender commandSender, String[] args) throws CommandException, IllegalCommandArgumentException {

        // return true immediately if no subcommands are defined
        if (arguments.size() == 0) {
            return new ArrayList<>();
        }

        // iterate arguments
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            Argument argument = arguments.get(i);

            // get corresponding argument type
            ArgumentType type = argument.getArgumentType();

            try {
                Object value;
                if (argument.isArray()) {
                    int z = i;
                    ArrayList<Object> entries = new ArrayList<>();
                    while (z < args.length) {
                        entries.add(type.tryCast(args[z++]));
                    }
                    if (!argument.isOptional() && entries.isEmpty()){
                        // non-optional arrays need at least one item
                        throw new IllegalCommandArgumentException();
                    }
                    value = !entries.isEmpty() ? type.toArray(entries) : null;
                } else {
                    value = args.length > i ? type.tryCast(args[i]) : null;
                }
                if (argument.isOptional()) {
                    value = Optional.ofNullable(value);
                }
                result.add(value);
            } catch (ClassCastException e) {
                type.sendFailureMessage(commandSender, args[i]);
                return null;
            }
        }
        return result;
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
}

