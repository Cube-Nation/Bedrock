package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.predefined.HelpCommand;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CommandTreeNestedNode extends CommandTreeNode {

    protected LinkedHashMap<String, CommandTreePathItem> subCommands = new LinkedHashMap<>();

    private HelpCommand helpCommand;

    public CommandTreeNestedNode(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) {
        try {
            return tryExecute(commandSender, treePath, args);
        } catch (IllegalCommandArgumentException e) {
            plugin.messages().invalidCommand(commandSender);
            if (helpCommand != null) {
                treePath.append(subCommands.get("help"));
                helpCommand.getJsonHelp(commandSender, treePath).get(0).send(commandSender);
            }
            return true;
        } catch (CommandException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean tryExecute(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) throws CommandException, IllegalCommandArgumentException {
        if (args.length == 0) {
            // Execute root command if present
            CommandTreePathItem rootCommandPath = subCommands.get("");
            if (rootCommandPath != null) {
                rootCommandPath.getNode().onCommand(commandSender, treePath, args);
                return true;
            }

            // Execute help command if present
            if (helpCommand == null) {
                throw new IllegalCommandArgumentException();
            }
            try {
                treePath.append(subCommands.get("help"));
                helpCommand.preExecute(commandSender, treePath, args);
            } catch (Exception e) {
                throw new CommandException("Help command could not be executed");
            }

            return true;
        }

        // Try execute
        return trySubCommandExecution(commandSender, treePath, args);
    }

    private boolean trySubCommandExecution(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) throws IllegalCommandArgumentException {
        String currentSubCommandLabel = args[0];
        String[] remainingArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        CommandTreePathItem pathItem = subCommands.get(currentSubCommandLabel);

        if (pathItem == null) {
            throw new IllegalCommandArgumentException();
        }
        treePath.append(pathItem);
        return pathItem.getNode().onCommand(commandSender, treePath, remainingArgs);
    }

    public <T extends CommandTreeNode> T addCommandHandler(Class<T> nodeClass, String... labels) throws CommandInitException {

        T node = createNode(nodeClass);

        if (labels.length == 0) {
            addNode(node, "");
            return node;
        }

        for (String l : labels) {
            addNode(node, l, labels);
        }

        return node;
    }

    private <T extends CommandTreeNode> T createNode(Class<T> nodeClass) {
        try {
            Constructor<T> constructor = nodeClass.getConstructor(FoundationPlugin.class);
            return constructor.newInstance(plugin);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            plugin.log(Level.SEVERE, "Creation of command '"+nodeClass.getName()+"' failed. Are you providing the right constructor?");
            e.printStackTrace();
            return null;
        }
    }

    private void addNode(CommandTreeNode node, String label, String... allLabels) throws CommandInitException {
        if (subCommands.containsKey(label)) {
            throw new CommandInitException(String.format("Cannot register subcommand '%s' since a subcommand by that name is already registered", label));
        }
        if (label.equals("") && !(node instanceof Command)) {
            throw new CommandInitException(String.format("Cannot register token less subcommand. Class has to extend %s", Command.class.getName()));
        }
        subCommands.put(label, CommandTreePathItem.create(node, label, allLabels));
    }

    public void addHelpCommand() throws CommandInitException {
        helpCommand = addCommandHandler(HelpCommand.class, "help");
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        Set<String> allCompletions = subCommands.keySet().stream().filter(s -> !s.equals("")).collect(Collectors.toSet());
        if (args.length == 0) {
            return allCompletions;
        }

        if (args.length >=2) {
            CommandTreePathItem next = subCommands.get(args[0]);
            if (next == null) {
                return List.of();
            }
            return next.getNode().onAutoComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        // Add token less completion if applicable
        CommandTreePathItem rootPathItem = subCommands.get("");
        if (rootPathItem != null) {
            rootPathItem.getNode().onAutoComplete(sender, args).forEach(allCompletions::add);
        }

        // Filter out any that do not match current input
        return allCompletions.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {
        ArrayList<JsonMessage> paths = new ArrayList<>();
        Set<CommandTreeNode> uniqueNodes = new HashSet<>();

        // Add all subcommands as separate lines
        for (Map.Entry<String, CommandTreePathItem> subCommand : subCommands.entrySet()) {
            // Handle token less subcommand separately
            if (subCommand.getKey().equals("")) {
                paths.addAll(0, subCommand.getValue().getNode().getJsonHelp(sender, treePath));
                continue;
            }

            // Skip aliases
            if (uniqueNodes.contains(subCommand.getValue().getNode())) {
                continue;
            }
            uniqueNodes.add(subCommand.getValue().getNode());

            CommandTreePath subPath = treePath.clone();
            subPath.append(subCommand.getValue());
            paths.addAll(subCommand.getValue().getNode().getJsonHelp(sender, subPath));
        }

        return paths;
    }
}
