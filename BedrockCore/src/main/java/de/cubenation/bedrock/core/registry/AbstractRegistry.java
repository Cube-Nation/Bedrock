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

package de.cubenation.bedrock.core.registry;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.BedrockCommandSender;
import de.cubenation.bedrock.core.exception.NoSuchRegisterableException;
import org.apache.commons.lang.StringUtils;

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

    protected static String getRegistryIdentifier(final FoundationPlugin plugin, final String ident, final BedrockCommandSender sender) {
        ArrayList<String> list = new ArrayList<String>() {{
            add(plugin.getPluginDescription().getName());
            add(ident);

            if (sender != null)
                add(sender.getName());
        }};
        return StringUtils.join(list, "|");
    }

    /*
     * register object
     */
    public void _register(FoundationPlugin plugin, String ident, BedrockCommandSender sender, Registerable object) {
        // Replace old if exists
        registry.put(getRegistryIdentifier(plugin, ident, sender), object);
    }

    public boolean _exists(FoundationPlugin plugin, String ident, BedrockCommandSender sender) {
        return registry.containsKey(getRegistryIdentifier(plugin, ident, sender));
    }

    public Registerable _get(FoundationPlugin plugin, String ident, BedrockCommandSender sender) throws NoSuchRegisterableException {
        if (!_exists(plugin, ident, sender))
            throw new NoSuchRegisterableException(getRegistryIdentifier(plugin, ident, sender));

        return registry.get(getRegistryIdentifier(plugin, ident, sender));
    }

    public void _remove(FoundationPlugin plugin, String ident, BedrockCommandSender sender) {
        registry.remove(getRegistryIdentifier(plugin, ident, sender));
    }

    @SuppressWarnings("unused")
    public static int size() {
        return registry.size();
    }

    @SuppressWarnings("unused")
    public static boolean containsKey(FoundationPlugin plugin, String ident, BedrockCommandSender sender) {
        return registry.containsKey(getRegistryIdentifier(plugin, ident, sender));
    }

    @SuppressWarnings("unused")
    public static Set<String> keySet() {
        return registry.keySet();
    }

}
