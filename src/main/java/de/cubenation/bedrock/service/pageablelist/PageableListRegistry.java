package de.cubenation.bedrock.service.pageablelist;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.registry.AbstractRegistry;
import de.cubenation.bedrock.registry.RegistryInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

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
    private PageableListRegistry() {
        timeout = BedrockPlugin.getInstance().getConfigService().getConfig().getInt("service.pageablelist.timeout");
    }

    /*
     * instance methods
     */
    public static PageableListRegistry getInstance () {
        return InstanceHolder.INSTANCE;
    }


    public static void register(final BasePlugin plugin, final String ident, final CommandSender sender, AbstractPageableListService object) {
        getInstance()._register(plugin, ident, sender, object);

        Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            public void run() {
                PageableListRegistry registry = PageableListRegistry.getInstance();
                registry.remove(plugin, ident, sender);
            }
        }, 60L, (long) 20 * timeout);
    }

    public static boolean exists(BasePlugin plugin, String ident, CommandSender sender) {
        return getInstance()._exists(plugin, ident, sender);
    }

    public AbstractPageableListService get(BasePlugin plugin, String ident, CommandSender sender) throws NoSuchRegisterableException {
        return (AbstractPageableListService) getInstance()._get(plugin, ident, sender);
    }

    public void remove(BasePlugin plugin, String ident, CommandSender sender) {
        getInstance()._remove(plugin, ident, sender);
    }

}
