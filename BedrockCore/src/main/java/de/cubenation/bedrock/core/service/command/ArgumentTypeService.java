package de.cubenation.bedrock.core.service.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.argument.type.ArgumentType;
import de.cubenation.bedrock.core.command.argument.type.IntegerArgument;
import de.cubenation.bedrock.core.command.argument.type.StringArgument;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;

import java.util.HashMap;

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
    }

    @Override
    public void reload() throws ServiceReloadException {
        // cannot be reloaded. why should it be anyway?
    }

    private HashMap<Class, ArgumentType> getPredefined() {
        return new HashMap<Class, ArgumentType>() {{
            put(String.class, new StringArgument(plugin));
            put(int.class, new IntegerArgument(plugin));
        }};
    }

    public ArgumentType getType(Class clazz) {
        if (!types.containsKey(clazz)) {
            return null;
        }
        return types.get(clazz);
    }
}
