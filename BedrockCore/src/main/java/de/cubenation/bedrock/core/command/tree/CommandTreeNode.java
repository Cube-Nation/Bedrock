package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.AutoCompletionExecutor;
import de.cubenation.bedrock.core.command.CommandExecutor;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import lombok.Getter;

import java.util.List;

// TODO: AutoCompletion
public abstract class CommandTreeNode implements CommandExecutor, AutoCompletionExecutor {

    @Getter
    protected final FoundationPlugin plugin;

    public CommandTreeNode(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath);
}
