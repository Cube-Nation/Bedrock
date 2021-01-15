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

package de.cubenation.bedrock.core.service.pageablelist;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.core.registry.AbstractRegistry;
import de.cubenation.bedrock.core.registry.RegistryInterface;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public class PageableListRegistry extends AbstractRegistry implements RegistryInterface {

    private static int timeout;

    public PageableListRegistry(FoundationPlugin plugin) {
        timeout = plugin.getConfigService().getReadOnlyConfig().getInt("service.pageablelist.timeout");
    }

    public void register(final FoundationPlugin plugin, final String ident, final BedrockChatSender sender, AbstractPageableListService object) {
        _register(plugin, ident, sender, object);

        // if the timeout is zero, the list is valid until a new list is registered for this CommandSender
        if (timeout > 0) {
            // TODO: tbd
//            Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
            remove(plugin, ident, sender);
//            }, 60L, (long) 20 * timeout);
        }
    }

    public boolean exists(FoundationPlugin plugin, String ident, BedrockChatSender sender) {
        return _exists(plugin, ident, sender);
    }

    public AbstractPageableListService get(FoundationPlugin plugin, String ident, BedrockChatSender sender) throws NoSuchRegisterableException {
        return (AbstractPageableListService) _get(plugin, ident, sender);
    }

    public void remove(FoundationPlugin plugin, String ident, BedrockChatSender sender) {
        _remove(plugin, ident, sender);
    }

    public static int getTimeout() {
        return timeout;
    }

}
