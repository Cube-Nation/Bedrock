/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.bukkit.api.registry;

import de.cubenation.bedrock.bukkit.api.BasePlugin;
import de.cubenation.bedrock.core.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.core.registry.Registerable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Cube-Nation
 * @version 1.0
 */
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

    @SuppressWarnings("unused")
    public static int size() {
        return registry.size();
    }

    @SuppressWarnings("unused")
    public static boolean containsKey(BasePlugin plugin, String ident, CommandSender sender) {
        return registry.containsKey(getRegistryIdentifier(plugin, ident, sender));
    }

    @SuppressWarnings("unused")
    public static Set<String> keySet() {
        return registry.keySet();
    }

}
