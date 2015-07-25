package de.cubenation.bedrock.registry;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractRegistry {

    protected static HashMap<String, Registerable> registry;

    static {
        registry = new HashMap<>();
    }

    protected static String getRegistryIdentifier(final BasePlugin plugin, final String ident, final CommandSender sender) {
        ArrayList<String> list = new ArrayList<String>() {{
            add(plugin.getDescription().getName());
            add(ident);

            if (sender != null)
                add(sender.getName());
        }};
        return StringUtils.join(list, "|");
    }


    /*
     * register object
     */
    public void _register(BasePlugin plugin, String ident, CommandSender sender, Registerable object) {
        // Replace old if exists
        registry.put(getRegistryIdentifier(plugin, ident, sender), object);
    }

    public boolean _exists(BasePlugin plugin, String ident, CommandSender sender) {
        return registry.containsKey(getRegistryIdentifier(plugin, ident, sender));
    }

    public Registerable _get(BasePlugin plugin, String ident, CommandSender sender) throws NoSuchRegisterableException {
        if (!_exists(plugin, ident, sender))
            throw new NoSuchRegisterableException(getRegistryIdentifier(plugin, ident, sender));

        return registry.get(getRegistryIdentifier(plugin, ident, sender));
    }

    public void _remove(BasePlugin plugin, String ident, CommandSender sender) {
        registry.remove(getRegistryIdentifier(plugin, ident, sender));
    }
}
