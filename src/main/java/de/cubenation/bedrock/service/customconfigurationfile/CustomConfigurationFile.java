package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.registry.Registerable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class CustomConfigurationFile implements Registerable {

	private String filename							= null;
	
	public static CustomConfigurationFile instance	= null;

	private YamlConfiguration yaml					= null;
	
	private File file								= null;

	private JavaPlugin plugin						= null;
	
	
	public CustomConfigurationFile(JavaPlugin plugin, String filename, HashMap<String,Object> data) throws IOException {
		this.plugin = plugin;
		this.setFilename(filename);
		
		this.file = new File(
			plugin.getDataFolder().getAbsolutePath() +
			java.lang.System.getProperty("file.separator") + 
			filename
		);
		
		if (!this.file.exists())
			this.save(data);
		
		instance 	= this;
	}
	
	private void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public static CustomConfigurationFile getInstance() {
		return instance;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void save(HashMap<String,Object> data) throws IOException {

		this.plugin.getLogger().info(String.format(
			"[Bedrock] Creating file %s for plugin %s",
			this.file.getAbsolutePath(),
			this.plugin.getName()
		));
		
		this.yaml = new YamlConfiguration();
		
		Iterator it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			this.yaml.set((String) pair.getKey(), pair.getValue());
			it.remove();
		}

		this.yaml.save(this.file.getAbsolutePath());		
	}
	
	
	public YamlConfiguration load() {
		return YamlConfiguration.loadConfiguration(this.file);
	}
	
	
	public YamlConfiguration reload() {
		this.plugin.getLogger().info(String.format(
			"[Bedrock] Reloading file %s from plugin %s",
			this.file.getAbsolutePath(),
			this.plugin.getName()
		));		
		return this.load();
	}	

}