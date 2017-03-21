package de.cubenation.api.bedrock.service.localization;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.BedrockPlugin;
import de.cubenation.api.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.api.bedrock.service.ServiceInterface;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.service.AbstractService;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The Localization Service
 *
 * @author Cube-Nation
 */
public final class LocalizationService extends AbstractService implements ServiceInterface {

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

        //this.getPlugin().log(Level.INFO, "  localization service: setting up " + this.toString());

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

    @SuppressWarnings("deprecation")
    /**
     * We suppress deprecation warnings in here, because handling the access to locale strings
     * is easier when we can use the built-in YAMLConfiguration methods.
     *
     * If we would use the Yamler features, the localization service could not easily access
     * localized messages.
     *
     * In the future there will be an abstract LocalizationConfig class that can manage this stuff
     */
    private void loadPluginLocaleFile() {
        this.plugin_data = this.getPlugin().getConfigService().getReadOnlyConfig(this.getRelativeLocaleFile());

        if (this.plugin_data == null)
            this.getPlugin().log(Level.SEVERE, String.format(
                    "  localization service: Could not find locale file %s in plugin %s",
                    this.getRelativeLocaleFile(),
                    this.getPlugin().getDescription().getName()
            ));
    }

    @SuppressWarnings("deprecation")
    /**
     * We suppress deprecation warnings in here, because handling the access to locale strings
     * is easier when we can use the built-in YAMLConfiguration methods.
     *
     * If we would use the Yamler features, the localization service could not easily access
     * localized messages.
     *
     * In the future there will be an abstract LocalizationConfig class that can manage this stuff
     */
    private void loadBedrockLocaleFile() throws ServiceInitException {
        this.bedrock_data = BedrockPlugin.getInstance().getConfigService().getReadOnlyConfig(this.getRelativeLocaleFile());

        if (this.bedrock_data == null)
            throw new ServiceInitException(String.format(
                    "Could not find locale file %s in plugin BedrockPlugin. Please restart server and use a supported locale",
                    this.getRelativeLocaleFile()
            ));
    }

    public String getTranslation(String path, String[] args) throws LocalizationNotFoundException {
        try {
            return this.getTranslationFromPlugin(path, args);
        } catch (LocalizationNotFoundException ignored) {
        }

        return this.getTranslationFromBedrock(path, args);
    }

    public String[] getTranslationStrings(String path, String[] args) throws LocalizationNotFoundException {
        try {
            return this.getTranslationStringsFromPlugin(path, args);
        } catch (LocalizationNotFoundException ignored) {
        }

        return this.getTranslationStringsFromBedrock(path, args);
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
        if (this.bedrock_data == null)
            return null;

        String s = this.bedrock_data.getString(path);
        if (s == null || s.isEmpty()) {
            throw new LocalizationNotFoundException(path);
        }

        return this.applyArgs(s, args);
    }

    @SuppressWarnings("unchecked")
    private String[] getTranslationStringsFromPlugin(String path, String[] args) throws LocalizationNotFoundException {
        if (this.plugin_data == null)
            return null;

        List<String> list = (List<String>) this.plugin_data.getList(path);
        if (list == null || list.size() == 0) {
            throw new LocalizationNotFoundException(path);
        }

        // create a copy!
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= list.size(); i++) {
            out.add(this.applyArgs(list.get(i - 1), args));
        }

        String[] s = new String[out.size()];
        s = out.toArray(s);
        return s;
    }

    @SuppressWarnings("unchecked")
    private String[] getTranslationStringsFromBedrock(String path, String[] args) throws LocalizationNotFoundException {
        if (this.bedrock_data == null)
            return null;

        List<String> list = (List<String>) this.bedrock_data.getList(path);
        if (list == null || list.size() == 0) {
            throw new LocalizationNotFoundException(path);
        }

        // create a copy!
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= list.size(); i++) {
            out.add(this.applyArgs(list.get(i - 1), args));
        }

        String[] s = new String[out.size()];
        s = out.toArray(s);
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
        if (args.length % 2 != 0)
            return s;

        for (int i = 0; i < args.length; i++) {
            s = s.replaceAll("%" + args[i] + "%", args[i + 1]);
            i++;
        }
        return s;
    }

    @Override
    public String toString() {
        return "LocalizationService{" +
                "locale='" + locale + '\'' +
                ", relative_locale_file='" + relative_locale_file + '\'' +
                ", plugin_data=" + plugin_data +
                ", bedrock_data=" + bedrock_data +
                '}';
    }
}
