package de.cubenation.bedrock.core.command.argument.type;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.AutoCompletionExecutor;
import de.cubenation.bedrock.core.exception.ArgumentTypeCastException;
import lombok.Getter;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class ArgumentType<T> implements AutoCompletionExecutor {

    @Getter
    protected final Class<?> genericClass = ((Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

    protected final FoundationPlugin plugin;

    protected ArgumentType(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract T tryCast(String input) throws ArgumentTypeCastException;

    @SuppressWarnings("unchecked")
    public Object toArray(List<Object> list) {
        Object[] array = (T[]) Array.newInstance(genericClass, list.size());
        return list.toArray(array);
    }
}
