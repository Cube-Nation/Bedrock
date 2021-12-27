package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public abstract class ArgumentType<T> {

    public abstract T tryCast(String input) throws ClassCastException;

    public abstract void sendFailureMessage(BedrockChatSender commandSender, String input);
}
