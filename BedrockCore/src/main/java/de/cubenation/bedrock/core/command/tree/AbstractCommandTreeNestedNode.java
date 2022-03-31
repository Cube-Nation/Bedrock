package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.CommandHandler;
import de.cubenation.bedrock.core.exception.CommandInitException;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class AbstractCommandTreeNestedNode extends CommandTreeNode {

    protected HashMap<String, CommandTreeNode> subCommands = new HashMap<>();

    public AbstractCommandTreeNestedNode(FoundationPlugin plugin, String label, CommandTreeNode previousNode) {
        super(plugin, label, previousNode);
    }

    @Override
    public boolean onCommand(BedrockChatSender commandSender, String[] args) {

        if (trySubCommands(commandSender, args)) {
            return true;
        }

        // Unknown command
        this.plugin.messages().invalidCommand(commandSender);

        JsonMessage jsonHelp = null; // TODO: plugin.messages().getHelpForSubCommand(commandSender, this.command);;
        if (jsonHelp == null) {
            this.plugin.messages().insufficientPermission(commandSender);
        } else {
            jsonHelp.send(commandSender);
        }
        return true;
    }

    abstract boolean trySubCommands(BedrockChatSender commandSender, String[] remainingArgs);

    public void addCommandHandler(Class<? extends CommandTreeNode> node, String... label) throws CommandInitException {

        CommandTreeNode node createNode(node, label.length == 0 ? "" , this)

        if (label.length == 0) {
            this.subCommands.put("", createNode(node, "", this));
            return;
        }

        for (String l : label) {
            this.subCommands.put(l, createNode(node, l, this));
        }
    }

    public void addCommandHandler(CommandTreeNode node, String label) {
        this.subCommands.put(label, node);
    }

    private CommandTreeNode createNode(Class<? extends CommandTreeNode> node, String label, CommandTreeNode previousNode) throws CommandInitException {
        try {
            Constructor<? extends CommandTreeNode> constructor = node.getConstructor(FoundationPlugin.class, String.class, CommandTreeNode.class);
            return constructor.newInstance(plugin, label, previousNode);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new CommandInitException(e);
        }
    }
}
