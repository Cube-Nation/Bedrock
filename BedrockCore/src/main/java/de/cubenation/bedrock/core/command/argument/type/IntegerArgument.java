package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

public class IntegerArgument extends ArgumentTypeWithRange<Integer> {

    public IntegerArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Integer tryCast(String input) throws ArgumentTypeCastException {
        try {
            int value = Integer.parseInt(input);
            if (value > maxRange) {
                throw new ArgumentTypeCastException(plugin.messages().getGreaterThan(input, maxRange));
            }
            if (value < minRange) {
                throw new ArgumentTypeCastException(plugin.messages().getLowerThan(input, minRange));
            }
            return value;
        } catch (NumberFormatException nfe) {
            throw new ArgumentTypeCastException(plugin.messages().getNoValidInt(input));
        }
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }

}
