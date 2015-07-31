package de.cubenation.bedrock.service.localization;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

/**
 * The Localization Service
 *
 * @author Cube-Nation
 */
public class LocalizationService extends AbstractService implements ServiceInterface {

    private String locale;

    private String relative_locale_file;

    private YamlConfiguration plugin_data;

    private YamlConfiguration bedrock_data;

    public LocalizationService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        this.setLocale();
        this.setRelativeLocaleFile("locale" + System.getProperty("file.separator") + this.getLocale() + ".yml");

        this.loadPluginLocaleFile();
        this.loadBedrockLocaleFile();
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

    public void setLocale() {
        this.locale = (String) this.getConfigurationValue("service.localization.locale", "en_US");
    }

    public String getLocale() {
        return this.locale;
    }

    private String getRelativeLocaleFile() {
        return this.relative_locale_file;
    }

    private void setRelativeLocaleFile(String relative_locale_file) {
        this.relative_locale_file = relative_locale_file;
    }

    private void loadPluginLocaleFile() {
        try {
            this.plugin_data = this.getPlugin().getConfigService().getConfig(this.getRelativeLocaleFile());

            System.out.println(this.plugin_data.getName());
            System.out.println(this.plugin_data.get("version"));

        } catch (NullPointerException e) {
            this.getPlugin().log(Level.SEVERE, String.format(
                    "Could not find locale file %s in plugin %s",
                    this.getRelativeLocaleFile(),
                    this.getPlugin().getDescription().getName()
            ));
            this.plugin_data = null;
        }
    }

    private void loadBedrockLocaleFile() throws ServiceInitException {
        try {
            this.bedrock_data = BedrockPlugin.getInstance().getConfigService().getConfig(this.getRelativeLocaleFile());
        } catch (NullPointerException e) {
            throw new ServiceInitException(String.format(
                    "Could not find locale file %s in plugin BedrockPlugin. Please restart server and use a supported locale",
                    this.getRelativeLocaleFile()
            ));
        }
    }

    public String getTranslation(String path, String[] args) throws LocalizationNotFoundException {
        try {
            return this.getTranslationFromPlugin(path, args);
        } catch (LocalizationNotFoundException ignored) {
        }

        return this.getTranslationFromBedrock(path, args);
    }

    private String getTranslationFromPlugin(String path, String[] args) throws LocalizationNotFoundException {
        if (this.plugin_data == null)
            return null;

        String s = this.plugin_data.getString(path);
        if (s == null || s.isEmpty()) {
            throw new LocalizationNotFoundException(path);
        }

        return this.applyArgs(s, args);
    }

    private String getTranslationFromBedrock(String path, String[] args) throws LocalizationNotFoundException {
        if (this.plugin_data == null)
            return null;

        String s = this.bedrock_data.getString(path);
        if (s == null || s.isEmpty()) {
            throw new LocalizationNotFoundException(path);
        }

        return this.applyArgs(s, args);
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
        if (args.length % 2 != 0)
            return s;

        for (int i = 0; i < args.length; i++) {
            s = s.replaceAll("%" + args[i] + "%", args[i + 1]);
            i++;
        }
        return s;
    }

}
