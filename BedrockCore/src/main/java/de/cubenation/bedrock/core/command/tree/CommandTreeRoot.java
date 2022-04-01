package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.List;

public class CommandTreeRoot extends CommandTreeNode {

    private final CommandTreeNode entrypoint;

    public CommandTreeRoot(FoundationPlugin plugin, CommandTreeNode entrypoint) {
        super(plugin, null);
        this.entrypoint = entrypoint;
    }

    @Override
    public boolean onCommand(BedrockChatSender sender, CommandTreePath treePath, String[] args) throws IllegalCommandArgumentException, InsufficientPermissionException, CommandException {
        return entrypoint.onCommand(sender, treePath, args);
    }

    @Override
    public List<JsonMessage> getJsonHelp(BedrockChatSender sender, CommandTreePath treePath) {
        return entrypoint.getJsonHelp(sender, treePath);
    }
}
