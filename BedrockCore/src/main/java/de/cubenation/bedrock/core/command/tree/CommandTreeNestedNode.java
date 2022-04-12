package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.predefined.HelpCommand;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import javax.swing.tree.TreePath;
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
        // TODO: add possibility for empty subcommand
        if (args.length == 0) {
            if (helpCommand == null) {
                throw new IllegalCommandArgumentException();
            }
            try {
                treePath.append(subCommands.get("help"));
                this.helpCommand.preExecute(commandSender, treePath, args);
            } catch (Exception e) {
                throw new CommandException("Help command could not be executed");
            }
            return true;
        }

        // Try execute
        if (trySubCommandExecution(commandSender, treePath, args)) {
            return true;
        }

        // Check for help
        if (helpCommand == null) {
            throw new IllegalCommandArgumentException();
        }

        // Display help
        List<JsonMessage> helpList = this.helpCommand.getFullHelpList(commandSender, treePath);
        if (helpList.isEmpty()) {
            throw new IllegalCommandArgumentException();
        }
        return true;
    }

    private boolean trySubCommandExecution(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) throws IllegalCommandArgumentException {
        String currentSubCommandLabel = args[0];
        String[] remainingArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length-1);
        CommandTreePathItem pathItem = subCommands.get(currentSubCommandLabel);

        if (pathItem == null) {
            throw new IllegalCommandArgumentException();
        }
        treePath.append(pathItem);
        return pathItem.getNode().onCommand(commandSender, treePath, remainingArgs);
    }

    public <T extends CommandTreeNode> T addCommandHandler(Class<T> nodeClass, String... labels) {

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

    private void addNode(CommandTreeNode node, String label, String... allLabels) {
        this.subCommands.put(label, CommandTreePathItem.create(node, label, allLabels));
    }

    public void addHelpCommand() {
        this.helpCommand = addCommandHandler(HelpCommand.class, "help");
    }

    public String[] getSubCommands() {
        return subCommands.keySet().toArray(String[]::new);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        Set<String> allSubcommands = this.subCommands.keySet();
        if (args.length == 0) {
            return allSubcommands;
        }

        if (args.length >=2) {
            CommandTreePathItem next = this.subCommands.get(args[0]);
            if (next == null) {
                return List.of();
            }
            return next.getNode().onAutoComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        // Filter out any that do not match current input
        return allSubcommands.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {
        ArrayList<JsonMessage> paths = new ArrayList<>();
        Set<CommandTreeNode> uniqueNodes = new HashSet<>();
        for (Map.Entry<String, CommandTreePathItem> subCommand : subCommands.entrySet()) {
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
