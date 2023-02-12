/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.service.localization;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.configuration.BedrockYaml;
import de.cubenation.bedrock.core.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The Localization Service
 *
 * @author Cube-Nation
 * @version 1.0
 */
@ToString
public class LocalizationService extends AbstractService {

    @Inject
    private ConfigService configService;

    @Inject(from = "Bedrock")
    private ConfigService fallbackConfigService;

    @Getter
    private String locale;

    @Getter @Setter
    private String relativeLocaleFile;

    private BedrockYaml pluginData;

    private BedrockYaml bedrockData;

    public LocalizationService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        setLocale();
        setRelativeLocaleFile("locale" + System.getProperty("file.separator") + getLocale() + ".yml");

        //this.getPlugin().log(Level.INFO, "  localization service: setting up " + this.toString());

        loadPluginLocaleFile();
        loadBedrockLocaleFile();
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

    public void setLocale() {
        locale = (String) getConfigurationValue("service.localization.locale", "en_US");
    }

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
        pluginData = configService.getReadOnlyConfig(getRelativeLocaleFile());

        if (pluginData == null)
            plugin.log(Level.SEVERE, String.format(
                    "  localization service: Could not find locale file %s in plugin %s",
                    getRelativeLocaleFile(),
                    plugin.getPluginDescription().getName()
            ));
    }

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
        bedrockData = fallbackConfigService.getReadOnlyConfig(getRelativeLocaleFile());

        if (bedrockData == null)
            throw new ServiceInitException(String.format(
                    "Could not find locale file %s in plugin BedrockPlugin. Please restart server and use a supported locale",
                    getRelativeLocaleFile()
            ));
    }

    public String getTranslation(String path, String[] args) throws LocalizationNotFoundException {
        try {
            return getTranslationFromPlugin(path, args);
        } catch (LocalizationNotFoundException ignored) {
        }

        return getTranslationFromBedrock(path, args);
    }

    public String[] getTranslationStrings(String path, String[] args) throws LocalizationNotFoundException {
        try {
            return getTranslationStringsFromPlugin(path, args);
        } catch (LocalizationNotFoundException ignored) {
        }

        return getTranslationStringsFromBedrock(path, args);
    }

    private String getTranslationFromPlugin(String path, String[] args) throws LocalizationNotFoundException {
        if (pluginData == null) {
            return null;
        }

        String s = pluginData.getString(path);
        if (s == null || s.isEmpty()) {
            throw new LocalizationNotFoundException(path);
        }

        return applyArgs(s, args);
    }

    private String getTranslationFromBedrock(String path, String[] args) throws LocalizationNotFoundException {
        if (bedrockData == null) {
            return null;
        }

        String s = bedrockData.getString(path);
        if (s == null || s.isEmpty()) {
            throw new LocalizationNotFoundException(path);
        }

        return applyArgs(s, args);
    }

    @SuppressWarnings("unchecked")
    private String[] getTranslationStringsFromPlugin(String path, String[] args) throws LocalizationNotFoundException {
        if (pluginData == null) {
            return null;
        }

        List<String> list = (List<String>) pluginData.getList(path);
        if (list == null || list.size() == 0) {
            throw new LocalizationNotFoundException(path);
        }

        // create a copy!
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= list.size(); i++) {
            out.add(applyArgs(list.get(i - 1), args));
        }

        String[] s = new String[out.size()];
        s = out.toArray(s);
        return s;
    }

    @SuppressWarnings("unchecked")
    private String[] getTranslationStringsFromBedrock(String path, String[] args) throws LocalizationNotFoundException {
        if (bedrockData == null) {
            return null;
        }

        List<String> list = (List<String>) bedrockData.getList(path);
        if (list == null || list.size() == 0) {
            throw new LocalizationNotFoundException(path);
        }

        // create a copy!
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= list.size(); i++) {
            out.add(applyArgs(list.get(i - 1), args));
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
        if (args.length % 2 != 0) {
            return s;
        }

        for (int i = 0; i < args.length; i++) {
            s = s.replaceAll("%" + args[i] + "%", args[i + 1]);
            i++;
        }
        return s;
    }
}
