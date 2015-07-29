package de.cubenation.bedrock.service.localization;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationRegistry;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.logging.Level;

public class LocalizationService implements ServiceInterface {

    private BasePlugin plugin;

    private String locale;

    private YamlConfiguration data;

    public LocalizationService(BasePlugin plugin) {
        this.setPlugin(plugin);
        this.setLocale(
                plugin.getPluginConfigService().getConfig().getString("service.localization.locale",
                BedrockPlugin.getInstance().getPluginConfigService().getConfig().getString("service.localization.locale",
                        "locale"))) //just in case someone fucked up the Bedrock config.yml
        ;
    }

    /*
    * Plugin Getter/Setter
    */
    private BasePlugin getPlugin() {
        return plugin;
    }

    private void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void init() throws ServiceInitException {
        // determine locale file and check if it exists
        try {
            this.loadLocaleFiles();
        } catch (IOException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

    /*
     * Locale Getter/Setter
     */
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    private void loadLocaleFiles() throws IOException {
        // check if there are any locale files

        // TODO: optimize stuff (duplicate check on BedrockPlugin)
        String[] language_files = {
                this.getLocale(),
                BedrockPlugin.getInstance().getPluginConfigService().getConfig().getString("service.localization.locale")
        };

        YamlConfiguration yc;
        for (String file : language_files) {
            try {
                this.getPlugin().log(Level.INFO, "Trying locale file " + file);
                yc = this.loadLocaleFile(file);
            } catch (NoSuchRegisterableException e) {
                this.getPlugin().log(Level.SEVERE, "Could not load file: " + e.getMessage());
                yc = null;
            }

            if (yc != null) {
                this.data = yc;
                return;
            }
        }

        // create a default locale file
        new DefaultLocale(this.getPlugin());
    }


    private YamlConfiguration loadLocaleFile(String file) throws NoSuchRegisterableException {
        String locale_file =
                BedrockPlugin.getInstance().getPluginConfigService().getConfig().getString("service.localization.locale_dir") +
                        java.lang.System.getProperty("file.separator") +
                        file + ".yml";

        return CustomConfigurationRegistry.get(this.plugin, locale_file, null).load();
    }


    public String getTranslation(String ident, String[] args) throws LocalizationNotFoundException {
        String s;
        try {
            s = this.data.getString(ident);
            if (s == null || s.isEmpty()) throw new NullPointerException();

        } catch (NullPointerException e) {
            //e.printStackTrace();
            throw new LocalizationNotFoundException(ident);
        }

        if (args.length % 2 == 0)
            s = this.applyArgs(s, args);

        return s;
    }

    /*
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
    */

    
    private String applyArgs(String s, String[] args) {
        for (int i = 0; i < args.length; i++) {
            s = s.replaceAll("%" + args[i] + "%", args[i + 1]);
            i++;
        }
        return s;
    }

}
