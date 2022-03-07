package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.UUID;

public class UuidArgument extends ArgumentType<UUID> {

    public UuidArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public UUID tryCast(String input) throws ClassCastException {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        plugin.messages().noValidUuid(commandSender, input);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }
}
