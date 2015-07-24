package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.registry.AbstractRegistry;
import de.cubenation.bedrock.registry.RegistryInterface;
import org.bukkit.command.CommandSender;

public class CustomConfigurationRegistry extends AbstractRegistry implements RegistryInterface{

    /*
     * make explicit singleton
     */
    private static final class InstanceHolder {
		static final CustomConfigurationRegistry INSTANCE = new CustomConfigurationRegistry();
	}

	/*
	 * private constructor
	 */
	private CustomConfigurationRegistry() { }

     /*
     * instance methods
     */
    public static CustomConfigurationRegistry getInstance () {
        return InstanceHolder.INSTANCE;
    }


    public static void register(BasePlugin plugin, String ident, CommandSender sender, CustomConfigurationFile object) {
        getInstance()._register(plugin, ident, sender, object);

        object.load();
    }

    public static boolean exists(BasePlugin plugin, String ident, CommandSender sender) {
        return getInstance()._exists(plugin, ident, sender);
    }

    public static CustomConfigurationFile get(BasePlugin plugin, String ident, CommandSender sender) throws NoSuchRegisterableException {
        return (CustomConfigurationFile) getInstance()._get(plugin, ident, sender);
    }

    public static void remove(BasePlugin plugin, String ident, CommandSender sender) {
        getInstance()._remove(plugin, ident, sender);
    }

}