package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class CommandTreeRoot extends CommandTreeNode {

    private final CommandTreeNode entrypoint;

    public CommandTreeRoot(FoundationPlugin plugin, String label, CommandTreeNode entrypoint) {
        super(plugin, label, null);
        this.entrypoint = entrypoint;
    }

    @Override
    public boolean onCommand(BedrockChatSender sender, String[] args) throws IllegalCommandArgumentException, InsufficientPermissionException, CommandException {
        return entrypoint.onCommand(sender, args);
    }
}
