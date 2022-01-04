package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class IntegerArgument extends ArgumentType<Integer> {

    public IntegerArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Integer tryCast(String input) throws ClassCastException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            throw new ClassCastException();
        }
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        plugin.messages().noValidInt(commandSender, input);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }
}
