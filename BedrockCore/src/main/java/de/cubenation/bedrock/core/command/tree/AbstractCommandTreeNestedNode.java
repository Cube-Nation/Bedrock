package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractCommandTreeNestedNode extends CommandTreeNode {

    protected HashMap<String, CommandTreeNode> subCommands = new HashMap<>();

    public AbstractCommandTreeNestedNode(FoundationPlugin plugin, CommandTreeNode previousNode) {
        super(plugin, previousNode);
    }

    @Override
    public boolean onCommand(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) {

        if (trySubCommands(commandSender, treePath, args)) {
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

    abstract boolean trySubCommands(BedrockChatSender commandSender, CommandTreePath treePath, String[] remainingArgs);

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {
        ArrayList<JsonMessage> paths = new ArrayList<>();
        for (Map.Entry<String, CommandTreeNode> subCommand : subCommands.entrySet()) {
            CommandTreePath subPath = treePath.clone();
            subPath.appendCall(subCommand.getKey());
            paths.addAll(subCommand.getValue().getJsonHelp(sender, subPath));
        }
        return paths;
    }

    public <T extends CommandTreeNode> T addCommandHandler(Class<T> nodeClass, String... label) {

        T node = createNode(nodeClass, this);

        if (label.length == 0) {
            this.subCommands.put("", node);
            return node;
        }

        for (String l : label) {
            this.subCommands.put(l, node);
        }

        return node;
    }

    public String[] getSubCommands() {
        return subCommands.keySet().toArray(String[]::new);
    }

    private <T extends CommandTreeNode> T createNode(Class<T> nodeClass, CommandTreeNode previousNode) {
        try {
            Constructor<T> constructor = nodeClass.getConstructor(FoundationPlugin.class, CommandTreeNode.class);
            return constructor.newInstance(plugin, previousNode);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            plugin.log(Level.SEVERE, "Initialization of command '"+nodeClass.getName()+"' failed. This shouldn't happen...");
            e.printStackTrace();
            return null;
        }
    }
}
