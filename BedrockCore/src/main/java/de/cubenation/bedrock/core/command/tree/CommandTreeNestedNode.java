package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.predefined.HelpCommand;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

public class CommandTreeNestedNode extends CommandTreeNode {

    protected HashMap<String, CommandTreePathItem> subCommands = new HashMap<>();

    private HelpCommand helpCommand;

    public CommandTreeNestedNode(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) throws IllegalCommandArgumentException, InsufficientPermissionException, CommandException {
        // TODO: add possibility for empty subcommand
        if (args.length == 0) {
            if (helpCommand == null) {
                this.plugin.messages().invalidCommand(commandSender);
                return false;
            }
            try {
                this.helpCommand.preExecute(commandSender, treePath, args);
            } catch (Exception e) {
                this.plugin.log(Level.INFO, "Error while executing help command. Shouldn't happen!");
                e.printStackTrace();
            }
            return true;
        }

        // Try execute
        if (trySubCommands(commandSender, treePath, args)) {
            return true;
        }

        // Check for help
        if (helpCommand == null) {
            plugin.messages().invalidCommand(commandSender);
            return true;
        }

        // Display help
        List<JsonMessage> helpList = this.helpCommand.getFullHelpList(commandSender, treePath);
        if (helpList.isEmpty()) {
            this.plugin.messages().invalidCommand(commandSender);
            return true;
        }
        return true;
    }

    private boolean trySubCommands(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) throws IllegalCommandArgumentException, InsufficientPermissionException, CommandException {
        String currentSubCommandLabel = args[0];
        String[] remainingArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length-1);
        CommandTreePathItem pathItem = subCommands.get(currentSubCommandLabel);

        if (pathItem == null) {
            return false;
        }
        return pathItem.getNode().onCommand(commandSender, treePath, remainingArgs);
    }

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {
        ArrayList<JsonMessage> paths = new ArrayList<>();
        for (Map.Entry<String, CommandTreePathItem> subCommand : subCommands.entrySet()) {
            CommandTreePath subPath = treePath.clone();
            subPath.append(subCommand.getValue());
            paths.addAll(subCommand.getValue().getNode().getJsonHelp(sender, subPath));
        }
        return paths;
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
}
