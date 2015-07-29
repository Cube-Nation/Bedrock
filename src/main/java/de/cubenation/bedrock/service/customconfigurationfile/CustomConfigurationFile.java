package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.registry.Registerable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public abstract class CustomConfigurationFile implements Registerable {

	private BasePlugin plugin						= null;

	private String filename							= null;
	
	public static CustomConfigurationFile instance	= null;

	private YamlConfiguration yaml					= null;
	
	private File file								= null;


	public CustomConfigurationFile(BasePlugin plugin, String filename, HashMap<String,Object> data) throws IOException {
		this.setPlugin(plugin);
		this.setFilename(filename);
		
		this.setFile(new File(
			plugin.getDataFolder().getAbsolutePath() +
			java.lang.System.getProperty("file.separator") + 
			filename
		));
		
		if (!this.file.exists())
			this.save(data);
		
		instance 	= this;
	}

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public BasePlugin getPlugin() {
        return this.plugin;
    }


	private void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}


	private void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	
	@SuppressWarnings("rawtypes")
	public void save(HashMap<String,Object> data) throws IOException {
        this.getPlugin().log(Level.INFO, String.format(
			"Creating file %s for plugin %s",
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
		this.getPlugin().log(Level.INFO, String.format(
                "[Bedrock] Reloading file %s from plugin %s",
                this.file.getAbsolutePath(),
                this.plugin.getName()
        ));
		return this.load();
	}	

}