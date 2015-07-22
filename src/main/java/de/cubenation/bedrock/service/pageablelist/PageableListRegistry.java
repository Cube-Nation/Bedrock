package de.cubenation.bedrock.service.pageablelist;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class PageableListRegistry {
	
	/*
	 * implicit synchronized singleton
	 */
	private static HashMap<CommandSender,PageableListService> registry
		= new HashMap<CommandSender,PageableListService>();

	private static final class InstanceHolder {
		static final PageableListRegistry INSTANCE = new PageableListRegistry();
	}

	/*
	 * private constructor
	 */
	private PageableListRegistry() {}
	
	/*
	 * instance methods
	 */
	public static PageableListRegistry getInstance () {
		return InstanceHolder.INSTANCE;
	}

	public void put(CommandSender sender, PageableListService service) {
		registry.put(sender, service);
	}
	
	public boolean has(CommandSender sender) {
		return registry.containsKey(sender);
	}

	public PageableListService get(CommandSender sender) {
		return registry.get(sender);
	}

	public void remove(CommandSender sender) {
		registry.remove(sender);
	}
}
