package de.cubenation.bedrock.localization;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.CustomConfigurationFileNotFoundException;
import de.cubenation.bedrock.exception.LocalizationFileNotFoundException;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationRegistry;

public class Localization {
	
	//private String locale			= null;
	
	private BasePlugin plugin 		= null;
		
	private Locale locale			= null;
	
	private String locale_file		= null;
	
	private YamlConfiguration data	= null;
	
	public Localization(BasePlugin plugin, String tkey, Locale locale) throws LocalizationFileNotFoundException {
		this.setPlugin(plugin);
		this.loadTranslation(locale);
	}
	
	private void loadTranslation(Locale locale) throws LocalizationFileNotFoundException {
		YamlConfiguration yc = null;
		String[] language_files = { locale.getLocale(), locale.getDefaultLocale() };
				
		for ( String file : language_files ) {
			String locale_file = this.locale.getLocalePath() +
					java.lang.System.getProperty("file.separator") +
					file + ".yml";
			try {
				yc = CustomConfigurationRegistry.get(locale_file);
			} catch (CustomConfigurationFileNotFoundException e) {
				continue;
			}

			if (yc != null) {
				this.setLocaleFile(file);
				break;
			}
		}

		if (yc == null)
			throw new LocalizationFileNotFoundException("Could not find a suitable locale file");
		
		this.data = yc;
	}
	
	public String getTranslation(String path) throws LocalizationNotFoundException {
		String s;
		try {
			s = this.data.getString(path);
		} catch (NullPointerException e) {
			throw new LocalizationNotFoundException(path);
		}
		if (s == null)
			throw new LocalizationNotFoundException(path);
		 
		s = ChatColor.translateAlternateColorCodes('&', s);
		return s;
	}
	
	public String getTranslation(String path, String[] args) throws LocalizationNotFoundException {
		String s = this.getTranslation(path);
		
		 // check even		
    	if (args.length % 2 == 1)
            return s;
        
    	return this.applyArgs(s, args);
	}
	
	public String[] getTranslationStrings(String path, String[] args) throws LocalizationNotFoundException {
		List<String> out = this.getTranslationList(path, args);
		
		String[] s = new String[args.length];
		s = out.toArray(s);
		return s;
	}

	@SuppressWarnings("unchecked")
	public List<String> getTranslationList(String path, String[] args) throws LocalizationNotFoundException {
		List<String> list = null;
		try {
			list = (List<String>) this.data.getList(path);
		} catch (NullPointerException e) {
			throw new LocalizationNotFoundException(path);
		}
		
		if (list == null || list.size() == 0) throw new LocalizationNotFoundException(path);
		
		// create a copy!
		List<String> out = new ArrayList<String>();
		for (int i = 1; i <= list.size(); i++) {
			out.add(this.applyArgs(list.get(i-1), args));
		}
		
		return out;
	}
	
	private String applyArgs(String s, String[] args) {
        for(int i = 0; i < args.length; i++){
            s = s.replaceAll("%" + args[i] + "%", args[i+1]);
            i++;
        }
		return ChatColor.translateAlternateColorCodes('&', s);		
	}








	/**
	 * @return the plugin
	 */
	public BasePlugin getPlugin() {
		return plugin;
	}








	/**
	 * @param plugin the plugin to set
	 */
	public void setPlugin(BasePlugin plugin) {
		this.plugin = plugin;
	}








	/**
	 * @return the locale_file
	 */
	public String getLocaleFile() {
		return locale_file;
	}








	/**
	 * @param locale_file the locale_file to set
	 */
	public void setLocaleFile(String locale_file) {
		this.locale_file = locale_file;
	}
	
}
