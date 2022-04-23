package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;

public class DoubleArgument extends ArgumentTypeWithRange<Double> {

    public DoubleArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Double tryCast(String input) throws ArgumentTypeCastException {
        try {
            double value = Double.parseDouble(input);
            if (value > maxRange) {
                throw new ArgumentTypeCastException(plugin.messages().getGreaterThan(input, maxRange));
            }
            if (value < minRange) {
                throw new ArgumentTypeCastException(plugin.messages().getLowerThan(input, minRange));
            }
            return value;
        } catch (NumberFormatException nfe) {
            throw new ArgumentTypeCastException(plugin.messages().getNoValidFloat(input));
        }
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }

}
