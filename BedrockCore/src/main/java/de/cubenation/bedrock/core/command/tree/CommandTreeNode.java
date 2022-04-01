package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.CommandExecutor;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;

import java.util.List;

// TODO: AutoCompletion
public abstract class CommandTreeNode implements CommandExecutor {

    @Getter
    protected final FoundationPlugin plugin;

    @Getter
    private final CommandTreeNode previousNode;

    public CommandTreeNode(FoundationPlugin plugin, CommandTreeNode previousNode) {
        this.plugin = plugin;
        this.previousNode = previousNode;
    }

    public abstract List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath);
}
