package de.cubenation.bedrock.service.customconfigurationfile;

import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import de.cubenation.bedrock.exception.CustomConfigurationFileNotFoundException;

public class CustomConfigurationRegistry {
	
	public static HashMap<String,CustomConfigurationFile> data		= new HashMap<String,CustomConfigurationFile>();

	public static void register(CustomConfigurationFile c) {
		CustomConfigurationRegistry.data.put((String) c.getFilename(), c);
		c.load();
	}

	public static boolean exists(String filename) {
		return (CustomConfigurationRegistry.data.containsKey(filename));
	}
	
	public static YamlConfiguration get(String filename) throws CustomConfigurationFileNotFoundException {
		if (CustomConfigurationRegistry.exists(filename)) {
			CustomConfigurationFile cc = (CustomConfigurationFile) CustomConfigurationRegistry.data.get(filename);
			return cc.load();
		}
		throw new CustomConfigurationFileNotFoundException();
	}
	
	public static CustomConfigurationFile getFileObject(String filename) throws CustomConfigurationFileNotFoundException {
		if (CustomConfigurationRegistry.exists(filename)) {
			return (CustomConfigurationFile) CustomConfigurationRegistry.data.get(filename);
		}
		throw new CustomConfigurationFileNotFoundException();
	}
	
	public static void reload(String filename) throws CustomConfigurationFileNotFoundException {
		if (CustomConfigurationRegistry.data.containsKey(filename)) {
			CustomConfigurationRegistry.data.get(filename).reload();
		}
		throw new CustomConfigurationFileNotFoundException();
	}
}
