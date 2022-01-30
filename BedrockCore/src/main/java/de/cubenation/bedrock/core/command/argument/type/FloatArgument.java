package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.List;

public class FloatArgument extends ArgumentType<Float> {

    public FloatArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Float tryCast(String input) throws ClassCastException {
        try {
            return Float.parseFloat(input);
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
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = (float) list.get(i);
        }
        return array;
    }
}
