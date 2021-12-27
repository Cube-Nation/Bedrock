package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class StringArgument extends ArgumentType<String> {

    @Override
    public String tryCast(String input) throws ClassCastException {
        return input;
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        // not gonna happen
    }
}
