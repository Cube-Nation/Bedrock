package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class StringArgument extends ArgumentType<String> {

    public StringArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public String tryCast(String input) throws ClassCastException {
        return input;
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        // not gonna happen
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }
}
