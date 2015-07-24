package de.cubenation.bedrock.service.localization;

import de.cubenation.bedrock.BasePlugin;

public class Locale {

	private BasePlugin plugin;

	private String locale;

	private String locale_path		= "locale";	
	
	public Locale(BasePlugin plugin) {
		this(plugin, null);
	}
	
	public Locale(BasePlugin plugin, String locale) {
		this.setPlugin(plugin);
		
		if (locale == null) {
			locale = this.getPlugin().getConfig().getString("locale");
		}
		
		this.setLocale(locale);
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
	
	public String getDefaultLocale() {
		return "en_US";
	}
	
	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the locale_path
	 */
	public String getLocalePath() {
		return locale_path;
	}

	/**
	 * @param locale_path the locale_path to set
	 */
	public void setLocalePath(String locale_path) {
		this.locale_path = locale_path;
	}
	
}