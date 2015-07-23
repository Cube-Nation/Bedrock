package de.cubenation.bedrock.service.pageablelist;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

@SuppressWarnings("unused")
public class PageableListRegistry {

	private static int timeout;

	/*
	 * implicit synchronized singleton
	 */
	private static HashMap<String,PageableListService> registry
		= new HashMap<>();

	private static final class InstanceHolder {
		static final PageableListRegistry INSTANCE = new PageableListRegistry();
	}

	/*
	 * private constructor
	 */
	private PageableListRegistry() {
        this.timeout = BedrockPlugin.getInstance().getConfig().getInt("service.pageablelist.timeout");
    }
	
	/*
	 * instance methods
	 */
	public static PageableListRegistry getInstance () {
		return InstanceHolder.INSTANCE;
	}

    @SuppressWarnings("unused")
	private String getRegistryIdentifier(BasePlugin plugin, String ident, CommandSender sender) {
		return plugin.getName() + "|" + ident + "|" + sender.getName();
	}

    @SuppressWarnings("unused")
	public void put(final BasePlugin plugin, final String ident, final CommandSender sender, PageableListService service, final int timeout) {
		registry.put(this.getRegistryIdentifier(plugin, ident, sender), service);

		Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            public void run() {
                PageableListRegistry registry = PageableListRegistry.getInstance();
                registry.remove(plugin, ident, sender);
            }
        }, 60L, (long) 20 * timeout);
	}

    @SuppressWarnings("unused")
	public void put(BasePlugin plugin, String ident, CommandSender sender, PageableListService service) {
		this.put(plugin, ident, sender, service, this.timeout);
	}

    @SuppressWarnings("unused")
	public boolean has(BasePlugin plugin, String ident, CommandSender sender) {
		return registry.containsKey(this.getRegistryIdentifier(plugin, ident, sender));
	}

    @SuppressWarnings("unused")
	public PageableListService get(BasePlugin plugin, String ident, CommandSender sender) {
		return registry.get(this.getRegistryIdentifier(plugin, ident, sender));
	}

    @SuppressWarnings("unused")
	public void remove(BasePlugin plugin, String ident, CommandSender sender) {
		registry.remove(this.getRegistryIdentifier(plugin, ident, sender));
	}

}
