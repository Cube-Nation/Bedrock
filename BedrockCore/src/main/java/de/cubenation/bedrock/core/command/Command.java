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
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.IngameCommand;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.annotation.condition.AnnotationCondition;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.argument.Argument;
import de.cubenation.bedrock.core.command.argument.Option;
import de.cubenation.bedrock.core.command.argument.type.ArgumentType;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.helper.CollectionUtil;
import de.cubenation.bedrock.core.helper.LengthComparator;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.parts.BedrockJson;
import de.cubenation.bedrock.core.translation.parts.JsonColor;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
public abstract class Command {

    @Getter
    protected FoundationPlugin plugin;

    @Getter
    protected CommandManager commandManager;

    @Getter @Setter
    private String description = "";

    @Getter
    private final ArrayList<String[]> subcommands = new ArrayList<>();

    @Getter
    private final ArrayList<de.cubenation.bedrock.core.command.argument.Argument> arguments = new ArrayList<>();

    private final ArrayList<de.cubenation.bedrock.core.command.argument.Argument> genericArguments = new ArrayList<>();
    private final HashMap<String, Option> options = new HashMap<>();

    private final ArrayList<de.cubenation.bedrock.core.authorization.Permission> runtimePermissions = new ArrayList<>();

    @Getter
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
    public Command(FoundationPlugin plugin, CommandManager commandManager) {
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

    /*
     * ###################################
     * # INITIALIZATION
     * ###################################
     */

    private void parseMethodAnnotations(Class<? extends Command> clazz) throws CommandInitException {
        // Description
        Description description = clazz.getAnnotation(Description.class);
        if (description != null) {
            this.setDescription(description.value());
        }

        // SubCommand/s
        Arrays.stream(clazz.getAnnotationsByType(SubCommand.class)).forEach(
                subCommand -> this.addSubCommand(subCommand.value())
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

    public void parseExecuteMethod() throws CommandInitException {
        // get possible execute method matches
        List<Method> matches = Arrays.stream(getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("execute"))
                .toList();

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

    @SuppressWarnings("unchecked")
    private void processExecuteParameter(Parameter parameter) throws CommandInitException {

        // Check if condition is fulfilled
        de.cubenation.bedrock.core.annotation.ArgumentConstraints constraints = parameter.getAnnotation(de.cubenation.bedrock.core.annotation.ArgumentConstraints.class);
        if (constraints != null && !this.checkArgumentCondition(constraints.Condition())) {
            return;
        }

        // Check if annotations are valid
        de.cubenation.bedrock.core.annotation.Argument argumentAnnotation = parameter.getAnnotation(de.cubenation.bedrock.core.annotation.Argument.class);
        de.cubenation.bedrock.core.annotation.Option optionAnnotation = parameter.getAnnotation(de.cubenation.bedrock.core.annotation.Option.class);
        boolean isOption = optionAnnotation != null;
        if (argumentAnnotation != null && isOption) {
            throw new CommandInitException("Argument "+parameter.getName()+" cannot have @Argument and @Option annotation at once");
        }

        // Get data type
        Class<?> clazz = parameter.getType();
        boolean isArray = clazz.isArray();
        if (isArray) {
            if (isOption) {
                throw new CommandInitException("Options do not support Arrays yet");
            }
            clazz = clazz.getComponentType();
        }
        if (isOption && clazz.equals(Boolean.class)) {
            // Expects no parameters
            clazz = null;
        }

        // Create argument object
        Argument argument;
        if (!isOption) {
            argument = new Argument(
                    this.getPlugin(),
                    (argumentAnnotation != null && !Objects.equals(argumentAnnotation.Description(), "")) ? argumentAnnotation.Description() : "no_description",
                    (argumentAnnotation !=  null && !Objects.equals(argumentAnnotation.Description(), "")) ? argumentAnnotation.Placeholder() : parameter.getName(),
                    argumentAnnotation != null && argumentAnnotation.Optional(),
                    isArray,
                    clazz
            );
        } else {
            argument = new Option(
                    this.getPlugin(),
                    optionAnnotation.Key() != null ? optionAnnotation.Key() : parameter.getName().toLowerCase(),
                    !Objects.equals(optionAnnotation.Description(), "") ? optionAnnotation.Description() : "no_description",
                    !Objects.equals(optionAnnotation.Placeholder(), "") ? optionAnnotation.Placeholder() : parameter.getName(),
                    clazz
            );
        }

        // Create permission
        de.cubenation.bedrock.core.annotation.Permission permission = parameter.getAnnotation(de.cubenation.bedrock.core.annotation.Permission.class);
        if (permission != null && !permission.Name().isEmpty()) {
            argument.setPermission(
                    this.createPermission(
                            permission.Name(),
                            permission.Role(),
                            permission.RoleName(),
                            permission.Description()
                    )
            );
        }

        this.addArgument(argument);
        if (isOption) {
            options.put(
                    optionAnnotation.Key() != null ? optionAnnotation.Key() : parameter.getName().toLowerCase(),
                    (Option) argument
            );
        } else {
            genericArguments.add(argument);
        }
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

    /*
     * ###################################
     * # GETTER & SETTER
     * ###################################
     */

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

    private void addArgument(de.cubenation.bedrock.core.command.argument.Argument argument) {
        this.arguments.add(argument);
    }

    /*
     * ###################################
     * # EXECUTION
     * ###################################
     */

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

    /**
     * Returns an ArrayList of casted objects matching the types-array or throws exception for invalid types.
     *
     * @param args the value of the arguments
     * @return ArrayList of casted objects
     */
    protected ArrayList<Object> tryCastInputToArgumentTypes(BedrockChatSender commandSender, String[] args) throws IllegalCommandArgumentException {

        // return true immediately if no subcommands are defined
        if (arguments.size() == 0) {
            return new ArrayList<>();
        }

        // collect option values
        HashMap<String, Object> optionValues = new HashMap<>();
        ArrayList<String> realArgsList = new ArrayList<>();
        if (options.size() > 0) {
            for (int i = 0; i < args.length; i++) {
                if (!args[i].startsWith("-") || !options.containsKey(args[i].substring(1))) {
                    realArgsList.add(args[i]);
                    continue;
                }
                String key = args[i].substring(1);
                Option option = options.get(key);
                Object value = null;
                if (option.getDataType() == null) {
                    value = true;
                } else {
                    if (i+1 >= args.length) continue; // No option parameter entered
                    ArgumentType<?> type = option.getArgumentType();
                    try {
                        value = type.tryCast(args[++i]);
                    } catch (ClassCastException e) {
                        type.sendFailureMessage(commandSender, args[i]);
                        return null;
                    }
                }
                optionValues.put(key, value);
            }
        }
        String[] realArgs = realArgsList.toArray(new String[0]);

        // iterate all arguments
        ArrayList<Object> result = new ArrayList<>();
        int i = 0;
        for (Argument argument : arguments) {

            // use pre-populated option value
            if (argument instanceof Option option) {
                Object defaultValue = option.getDataType() == null ? false : null;
                Object value = optionValues.getOrDefault(option.getRuntimeKey(), defaultValue);
                result.add(value);
                continue;
            }

            // get argument value
            ArgumentType<?> type = argument.getArgumentType();
            try {
                Object value;
                if (argument.isArray()) {
                    // ARRAY
                    int z = i;
                    ArrayList<Object> entries = new ArrayList<>();
                    while (z < realArgs.length) {
                        entries.add(type.tryCast(realArgs[z++]));
                    }
                    if (!argument.isOptional() && entries.isEmpty()){
                        // non-optional arrays need at least one item
                        throw new IllegalCommandArgumentException();
                    }
                    value = type.toArray(entries); // return empty array and NOT null! Java conventions
                } else {
                    // NON ARRAY
                    value = realArgs.length > i ? type.tryCast(realArgs[i]) : null;
                }
                result.add(value);
            } catch (ClassCastException e) {
                type.sendFailureMessage(commandSender, realArgs[i]);
                return null;
            }
            i++;
        }
        return result;
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

    public boolean performPreArgumentCheck() {
        return true;
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

    /**
     * Define the priority to change the help order
     * Smaller = more important.
     *
     * @return priority
     */
    public Integer getHelpPriority() {
        return 0;
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
                if (argument instanceof Option option) {
                    result.add(BedrockJson.JsonWithText(option.getKey()).color(JsonColor.SECONDARY));
                    result.add(BedrockJson.Space());
                }

                /*
                 * Argument placeholder
                 */

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

    /*
     * ###################################
     * # AUTO COMPLETION
     * ###################################
     */

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

        ArrayList<String> autoCompletion = new ArrayList<>();

        // starts with '-'? autocomplete option keys itself
        CollectionUtil.addAllIfNotNull(autoCompletion, getAutoCompletionFromOptions(args));

        // previous is option key and has value? autocomplete option value else normal argument
        List<String> autoCompletionForOptionValues = getAutoCompletionFromOptionValues(sender, args);
        if (autoCompletionForOptionValues != null) {
            autoCompletion.addAll(autoCompletionForOptionValues);
        } else {
            CollectionUtil.addAllIfNotNull(autoCompletion, getAutoCompletionFromArguments(sender, args));
        }

        return autoCompletion;
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

    private List<String> getAutoCompletionFromOptions(String[] args) {
        if (args.length < 1) {
            return null;
        }

        String currentArg = args[args.length - 1];
        if (!currentArg.startsWith("-")) {
            return null;
        }

        return options.keySet().stream()
                .map(s -> "-"+s)
                .filter(s -> s.toLowerCase().startsWith(currentArg.toLowerCase()))
                .toList();
    }

    private List<String> getAutoCompletionFromOptionValues(BedrockChatSender sender, String[] args) {
        if (args.length < 2) {
            return null;
        }

        String prevArg = args[args.length - 2];
        String prevArgKey = prevArg.substring(1);
        if (!prevArg.startsWith("-") || !options.containsKey(prevArgKey) || !options.get(prevArgKey).hasParameter()) {
            return null;
        }

        List<String> autoCompletion = getAutoCompletionForArgument(sender, args, options.get(prevArg.substring(1)));
        // do not return null in case of no auto-completion results! option key is still valid
        return autoCompletion != null ? autoCompletion : new ArrayList<>();
    }

    private List<String> getAutoCompletionFromArguments(BedrockChatSender sender, String[] args) {
        String[] realArgs = Arrays.copyOfRange(args, this.getSubcommands().size(), args.length);
        realArgs = this.filterOutOptions(realArgs);

        int argIndex = realArgs.length - 1;
        if (argIndex < 0 || argIndex >= this.genericArguments.size()) {
            return null;
        }

        Argument argument = this.genericArguments.get(argIndex);

        List<String> autoCompletion = this.getAutoCompletionForArgument(sender, realArgs, argument);
        if (autoCompletion == null) {
            return null;
        }

        // If the user typed a part of the command, return the matching
        String currentArg = args[args.length - 1];
        return autoCompletion.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg.toLowerCase()))
                .toList();
    }

    private List<String> getAutoCompletionForArgument(BedrockChatSender sender, String[] args, Argument argument) {
        ArgumentType<?> argumentType = argument.getArgumentType();
        if (argumentType == null) {
            return null;
        }
        Iterable<String> autoCompletionFromArguments = argumentType.onAutoComplete(sender, args);
        if (autoCompletionFromArguments == null) {
            return null;
        }

        ArrayList<String> autoCompletion = new ArrayList<>();
        autoCompletionFromArguments.forEach(autoCompletion::add);

        return autoCompletion;
    }

    private String[] filterOutOptions(String[] args) {
        ArrayList<String> realArgsList = new ArrayList<>();
        if (options.size() > 0) {
            for (int i = 0; i < args.length; i++) {
                if (!args[i].startsWith("-") || !options.containsKey(args[i].substring(1))) {
                    realArgsList.add(args[i]);
                    continue;
                }
                String key = args[i].substring(1);
                if (options.get(key).hasParameter()) i++;
            }
        }
        return realArgsList.toArray(new String[0]);
    }
}

