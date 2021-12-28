package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumArgument<T extends Enum<T>> extends ArgumentType<T> {

    public EnumArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public T tryCast(String input) throws ClassCastException {
        try {
            return Enum.valueOf(genericClass, input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public void sendFailureMessage(BedrockChatSender commandSender, String input) {
        plugin.messages().noValidEnumConstant(commandSender, input, getEnumConstants());
    }

    private List<String> getEnumConstants() {
        return Arrays.stream(genericClass.getEnumConstants()).map(t -> t.toString()).collect(Collectors.toList());
    }
}
