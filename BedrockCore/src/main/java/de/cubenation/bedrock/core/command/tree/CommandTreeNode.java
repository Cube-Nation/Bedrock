package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.CommandExecutor;
import lombok.Getter;

// TODO: AutoCompletion
public abstract class CommandTreeNode implements CommandExecutor {

    @Getter
    protected final FoundationPlugin plugin;

    @Getter
    private final String label;

    @Getter
    private final CommandTreeNode previousNode;

    public CommandTreeNode(FoundationPlugin plugin, String label, CommandTreeNode previousNode) {
        this.plugin = plugin;
        this.label = label;
        this.previousNode = previousNode;
    }
}
