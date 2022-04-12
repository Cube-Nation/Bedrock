package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class StringArgument extends ArgumentTypeWithRange<String> {

    public StringArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public String tryCast(String input) throws ArgumentTypeCastException {
        if (input.length() > maxRange) {
            throw new ArgumentTypeCastException(plugin.messages().getStringTooLong(input, maxRange));
        }
        if (input.length() < minRange) {
            throw new ArgumentTypeCastException(plugin.messages().getStringTooShort(input, minRange));
        }
        return input;
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }
}
