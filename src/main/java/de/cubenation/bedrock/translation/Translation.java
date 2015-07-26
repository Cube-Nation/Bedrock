package de.cubenation.bedrock.translation;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.service.localization.LocalizationService;

public class Translation {

    private String locale_ident;

    private String[] locale_args;

    private final LocalizationService service;


    public Translation(BasePlugin plugin, String locale_ident) {
        this(plugin, locale_ident, new String[]{});
    }

    public Translation(BasePlugin plugin, String locale_ident, String[] locale_args) {
        this.service    = plugin.getLocalizationService();

        this.setLocale_ident(locale_ident);
        this.setLocale_args(locale_args);
    }

    private String getLocale_ident() {
        return locale_ident;
    }

    private void setLocale_ident(String locale_ident) {
        this.locale_ident = locale_ident;
    }

    private String[] getLocale_args() {
        return locale_args;
    }

    private void setLocale_args(String[] locale_args) {
        this.locale_args = locale_args;
    }

    public String getTranslation() {
        try {
            return this.service.getTranslation(this.getLocale_ident(), this.getLocale_args()).trim();
        } catch (LocalizationNotFoundException e) {
            // we do not return null to aboid NullPointerExceptions.
            // If you see an empty string somewhere
            //  a) the locale file is damaged/incomplete - try deleting it and restart the server
            //  b) check if the plugin refers to the correct path in the YamlConfiguration object
            return "";
        }
    }
}
