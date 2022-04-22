package de.cubenation.bedrock.core.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.IngameCommand;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.Range;
import de.cubenation.bedrock.core.annotation.condition.AnnotationCondition;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.argument.Argument;
import de.cubenation.bedrock.core.command.argument.Option;
import de.cubenation.bedrock.core.command.argument.type.ArgumentType;
import de.cubenation.bedrock.core.command.tree.CommandTreePath;
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.exception.*;
import de.cubenation.bedrock.core.helper.CollectionUtil;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.Translation;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Level;

/**
 * Abstract class for command executor classes
 *
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public abstract class Command extends CommandTreeNode {

    @Getter @Setter
    private String description = "";

    @Getter
    private final ArrayList<de.cubenation.bedrock.core.command.argument.Argument> arguments = new ArrayList<>();

    private final ArrayList<de.cubenation.bedrock.core.command.argument.Argument> genericArguments = new ArrayList<>();
    private final HashMap<String, Option> options = new HashMap<>();

    private final ArrayList<de.cubenation.bedrock.core.authorization.Permission> runtimePermissions = new ArrayList<>();

    @Getter
    private boolean isIngameCommandOnly = false;

    private boolean usesCommandTreeContext;

    private Method executeMethod;

    /**
     * The class constructor for all Bedrock commands.
     * <p>
     * Reads all annotations from #execute() and enables features
     * TBD
     */
    public Command(FoundationPlugin plugin) {
        super(plugin);

        // read annotations from execute methods
        try {
            this.parseMethodAnnotations(this.getClass());
            this.parseExecuteMethod();
        } catch (CommandInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {

        if (!hasPermission(sender)) {
            return null; // hide command from help
        }

        JsonMessage line = plugin.messages().getHelpForSubCommand(sender, treePath, this);
        return Collections.singletonList(line);
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
            throw new CommandInitException("Execute method not found. Please contact plugin author.");
        } else if (matches.size() > 1) {
            throw new CommandInitException("Multiple execute methods found. Please contact plugin author.");
        }
        this.executeMethod = matches.get(0);

        // get method parameters
        Parameter[] executeParameters = this.executeMethod.getParameters();
        // check for bedrockChatSender
        if (executeParameters.length == 0 || !executeParameters[0].getType().equals(BedrockChatSender.class)) {
            throw new CommandInitException("Execute method needs to start with BedrockChatSender parameter. Please contact plugin author.");
        }
        usesCommandTreeContext = (executeParameters.length > 1 && executeParameters[1].getType().equals(CommandTreePath.class));

        // remove BedrockChatSender from parameters
        executeParameters = Arrays.copyOfRange(executeParameters, usesCommandTreeContext ? 2 : 1, executeParameters.length);

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
                throw new CommandInitException("Invalid execute method. Optional parameters need to be last.");
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

        Range rangeAnnotation = getClass().getAnnotation(Range.class);

        // Create argument object
        Argument argument;
        if (!isOption) {
            argument = new Argument(
                    this.getPlugin(),
                    (argumentAnnotation != null && !argumentAnnotation.Description().equals("")) ? argumentAnnotation.Description() : "no_description",
                    (argumentAnnotation !=  null && !argumentAnnotation.Description().equals("")) ? argumentAnnotation.Placeholder() : parameter.getName(),
                    argumentAnnotation != null && argumentAnnotation.Optional(),
                    isArray,
                    clazz,
                    rangeAnnotation
            );
        } else {
            argument = new Option(
                    this.getPlugin(),
                    optionAnnotation.Key() != null ? optionAnnotation.Key() : parameter.getName().toLowerCase(),
                    !optionAnnotation.Description().equals("") ? optionAnnotation.Description() : "no_description",
                    !optionAnnotation.Description().equals("") ? optionAnnotation.Placeholder() : parameter.getName(),
                    optionAnnotation.Hidden(),
                    clazz,
                    rangeAnnotation
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
                    optionAnnotation.Key() != null ? new Translation(plugin, optionAnnotation.Key()).getTranslation() : parameter.getName().toLowerCase(),
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

    @Override
    public boolean onCommand(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) {
        try {
            this.preExecute(commandSender, treePath, args);
        } catch (CommandException e) {
            e.printStackTrace();
        } catch (IllegalCommandArgumentException e) {
            plugin.messages().invalidCommand(commandSender);
            getJsonHelp(commandSender, treePath).get(0).send(commandSender);
        } catch (InsufficientPermissionException e) {
            plugin.messages().insufficientPermission(commandSender);
        }
        return true;
    }

    public void preExecute(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        if (!this.hasPermission(commandSender)) {
            throw new InsufficientPermissionException();
        }

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
        if (usesCommandTreeContext) {
            executeParameterValues.add(1, treePath);
        }

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
     * Returns an ArrayList of cast objects matching the types-array or throws exception for invalid types.
     *
     * @param args the value of the arguments
     * @return ArrayList of cast objects
     */
    protected ArrayList<Object> tryCastInputToArgumentTypes(BedrockChatSender commandSender, String[] args) throws IllegalCommandArgumentException {

        // return true immediately if no subcommands are defined
        if (arguments.size() == 0) {
            return new ArrayList<>();
        }

        // collect option values
        HashMap<String, Object> optionValues = new HashMap<>();
        String[] realArgs;
        if (options.size() > 0) {
            ArrayList<String> realArgsList = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                if (!args[i].startsWith("-") || !options.containsKey(args[i].substring(1))) {
                    realArgsList.add(args[i]);
                    continue;
                }
                String key = args[i].substring(1);
                Option option = options.get(key);
                Object value;
                if (option.getDataType() == null) {
                    value = true;
                } else {
                    if (i+1 >= args.length) continue; // No option parameter entered
                    ArgumentType<?> type = option.getArgumentType();
                    try {
                        value = type.tryCast(args[++i]);
                    } catch (ArgumentTypeCastException e) {
                        e.getFailureMessage().send(commandSender);
                        return null;
                    }
                }
                optionValues.put(key, value);
            }
            realArgs = realArgsList.toArray(new String[0]);
        } else {
            realArgs = args;
        }

        // iterate all arguments
        ArrayList<Object> result = new ArrayList<>();
        int i = 0;
        for (Argument argument : arguments) {

            // use pre-populated option value
            if (argument instanceof Option option) {
                Object defaultValue = option.getDataType() == null ? false : null;
                Object value = optionValues.getOrDefault(option.getKey(), defaultValue);
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
            } catch (ArgumentTypeCastException e) {
                e.getFailureMessage().send(commandSender);
                return null;
            }
            i++;
        }
        return result;
    }

    public boolean performPreArgumentCheck() {
        return true;
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
    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {

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
        String[] realArgs = this.filterOutOptions(args);

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
        if (options.isEmpty()) {
            return args;
        }

        ArrayList<String> realArgsList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-") || !options.containsKey(args[i].substring(1))) {
                realArgsList.add(args[i]);
                continue;
            }
            String key = args[i].substring(1);
            if (options.get(key).hasParameter()) i++;
        }
        return realArgsList.toArray(new String[0]);
    }
}

