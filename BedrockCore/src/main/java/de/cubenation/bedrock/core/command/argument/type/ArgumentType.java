package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;

public abstract class ArgumentType<T> {

    @Getter
    protected final Class<T> genericClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);;

    protected final FoundationPlugin plugin;

    protected ArgumentType(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract T tryCast(String input) throws ClassCastException;

    public abstract void sendFailureMessage(BedrockChatSender commandSender, String input);
}
