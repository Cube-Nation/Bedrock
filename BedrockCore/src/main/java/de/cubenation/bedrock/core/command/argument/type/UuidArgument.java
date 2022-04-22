package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.UUID;

public class UuidArgument extends ArgumentType<UUID> {

    public UuidArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public UUID tryCast(String input) throws ArgumentTypeCastException {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            throw new ArgumentTypeCastException(plugin.messages().getNoValidUuid(input));
        }
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }
}
