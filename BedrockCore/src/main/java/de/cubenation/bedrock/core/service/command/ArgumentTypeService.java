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
import java.util.HashMap;
import java.util.UUID;

public class ArgumentTypeService extends AbstractService {

    private final FoundationPlugin plugin;

    private HashMap<Class, ArgumentType> types = new HashMap<>();

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

    private HashMap<Class, ArgumentType> getPredefined() {
        return new HashMap<>() {{
            put(String.class, new StringArgument(plugin));
            put(int.class, new IntegerArgument(plugin));
            put(float.class, new FloatArgument(plugin));
            put(double.class, new DoubleArgument(plugin));
            put(UUID.class, new UuidArgument(plugin));
            put(BedrockPlayer.class, new BedrockPlayerArgument(plugin));
        }};
    }

    public ArgumentType getType(Class<?> clazz) {
        if (!types.containsKey(clazz)) {
            return null;
        }
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

    private void addArgumentType(Class<? extends ArgumentType> clazz) throws ArgumentTypeAlreadyExistsException, ServiceInitException {
        if (this.exists(clazz))
            throw new ArgumentTypeAlreadyExistsException(clazz.toString());

        ArgumentType instance;
        try {
            Constructor<? extends ArgumentType> constructor = clazz.getConstructor(FoundationPlugin.class);
            instance = constructor.newInstance(plugin);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInitException(e.getMessage());
        }
        this.types.put(instance.getGenericClass(), instance);
    }

    private boolean exists(Class<? extends ArgumentType> clazz) {
        return this.types.containsKey(clazz);
    }
}
