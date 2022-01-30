package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.List;

public class DoubleArgument extends ArgumentType<Double> {

    public DoubleArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Double tryCast(String input) throws ClassCastException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException nfe) {
            throw new ClassCastException();
        }
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        plugin.messages().noValidFloat(commandSender, input);
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }

    @Override
    public Object toArray(List<Object> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = (double) list.get(i);
        }
        return array;
    }
}
