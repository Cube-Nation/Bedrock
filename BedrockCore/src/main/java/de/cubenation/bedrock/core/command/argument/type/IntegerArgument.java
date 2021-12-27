package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class IntegerArgument extends ArgumentType<Integer> {

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
        commandSender.sendMessage("keine ganzzahl"); // TODO: Message
    }
}
