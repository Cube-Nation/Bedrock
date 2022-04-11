package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;

import java.util.List;

public class CommandTreeRoot extends CommandTreeNode {

    @Getter
    protected final CommandTreePathItem entrypoint;

    public CommandTreeRoot(FoundationPlugin plugin, CommandTreePathItem entrypoint) {
        super(plugin);
        this.entrypoint = entrypoint;
    }

    @Override
    public boolean onCommand(BedrockChatSender sender, CommandTreePath treePath, String[] args) throws IllegalCommandArgumentException, InsufficientPermissionException, CommandException {
        return entrypoint.getNode().onCommand(sender, treePath, args);
    }

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {
        return entrypoint.getNode().getJsonHelp(sender, treePath);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return entrypoint.getNode().onAutoComplete(sender, args);
    }
}
