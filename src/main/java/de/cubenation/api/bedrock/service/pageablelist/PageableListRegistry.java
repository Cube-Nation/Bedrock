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

package de.cubenation.api.bedrock.service.pageablelist;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.api.bedrock.registry.AbstractRegistry;
import de.cubenation.api.bedrock.registry.RegistryInterface;
import de.cubenation.plugin.bedrock.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public class PageableListRegistry extends AbstractRegistry implements RegistryInterface {

	private static int timeout;

    /*
     * make explicit singleton
     */
    private static final class InstanceHolder {
        static final PageableListRegistry INSTANCE = new PageableListRegistry();
    }

    /*
     * private constructor
     */
    @SuppressWarnings("deprecation")
    private PageableListRegistry() {
        timeout = BedrockPlugin.getInstance().getConfigService().getReadOnlyConfig().getInt("service.pageablelist.timeout");
    }

    /*
     * instance methods
     */
    public static PageableListRegistry getInstance () {
        return InstanceHolder.INSTANCE;
    }


    public static void register(final BasePlugin plugin, final String ident, final CommandSender sender, AbstractPageableListService object) {
        getInstance()._register(plugin, ident, sender, object);

        // if the timeout is zero, the list is valid until a new list is registered for this CommandSender
        if (timeout > 0) {
            Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                PageableListRegistry registry = PageableListRegistry.getInstance();
                remove(plugin, ident, sender);
            }, 60L, (long) 20 * timeout);
        }
    }

    public static boolean exists(BasePlugin plugin, String ident, CommandSender sender) {
        return getInstance()._exists(plugin, ident, sender);
    }

    public static AbstractPageableListService get(BasePlugin plugin, String ident, CommandSender sender) throws NoSuchRegisterableException {
        return (AbstractPageableListService) getInstance()._get(plugin, ident, sender);
    }

    public static void remove(BasePlugin plugin, String ident, CommandSender sender) {
        getInstance()._remove(plugin, ident, sender);
    }

    public static int getTimeout() {
        return timeout;
    }

}
