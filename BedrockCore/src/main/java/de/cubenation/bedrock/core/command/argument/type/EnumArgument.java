package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumArgument<T extends Enum<T>> extends ArgumentType<T> {

    public EnumArgument(FoundationPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T tryCast(String input) throws ArgumentTypeCastException {
        try {
            return Enum.valueOf((Class<T>) genericClass, input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentTypeCastException(plugin.messages().getNoValidEnumConstant(input, getEnumConstants()));
        }
    }

    private List<String> getEnumConstants() {
        return Arrays.stream(genericClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public Iterable<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return getEnumConstants();
    }
}
