package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class CommandTreeNestedNode extends AbstractCommandTreeNestedNode {

    public CommandTreeNestedNode(FoundationPlugin plugin, String label, CommandTreeNode previousNode) {
        super(plugin, label, previousNode);
    }

    @Override
    protected boolean trySubCommands(BedrockChatSender commandSender, String[] args) {
        // TODO: add possibility for empty subcommand
        if (args.length == 0) {
            return false;
        }

        return false;
    }
}
