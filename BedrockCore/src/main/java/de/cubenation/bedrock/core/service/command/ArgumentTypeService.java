package de.cubenation.bedrock.core.service.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.argument.type.*;
import de.cubenation.bedrock.core.exception.ArgumentTypeAlreadyExistsException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.UUID;

public class ArgumentTypeService extends AbstractService {

    private final FoundationPlugin plugin;

    private final HashMap<Class<?>, Class<? extends ArgumentType<?>>> types = new HashMap<>();

    public ArgumentTypeService(FoundationPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void init() throws ServiceInitException {
        types.putAll(getPredefined());
        addCustomTypes();
    }

    @Override
    public void reload() throws ServiceReloadException {
        // cannot be reloaded. why should it be anyway?
    }

    private HashMap<Class<?>, Class<? extends ArgumentType<?>>> getPredefined() {
        return new HashMap<>() {{
            put(String.class, StringArgument.class);
            put(Integer.class, IntegerArgument.class);
            put(Float.class, FloatArgument.class);
            put(Double.class, DoubleArgument.class);
            put(UUID.class, UuidArgument.class);
            put(BedrockPlayer.class, BedrockPlayerArgument.class);
        }};
    }

    public Class<? extends ArgumentType<?>> getType(Class<?> clazz) {
        return types.get(clazz);
    }

    private void addCustomTypes() throws ServiceInitException {
        try {
            // find and register custom ArgumentType annotations
            for (de.cubenation.bedrock.core.annotation.ArgumentType type : plugin.getClass().getAnnotationsByType(de.cubenation.bedrock.core.annotation.ArgumentType.class)) {
                this.addArgumentType(type.value());
            }

        } catch (ArgumentTypeAlreadyExistsException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    private void addArgumentType(Class<? extends ArgumentType<?>> clazz) throws ArgumentTypeAlreadyExistsException, ServiceInitException {
        if (this.exists(clazz))
            throw new ArgumentTypeAlreadyExistsException(clazz.toString());

        Class<?> genericClass = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        types.put(genericClass, clazz);
    }

    private boolean exists(Class<? extends ArgumentType<?>> clazz) {
        return this.types.containsValue(clazz);
    }
}
