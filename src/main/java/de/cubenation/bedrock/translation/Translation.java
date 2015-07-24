package de.cubenation.bedrock.translation;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.service.localization.LocalizationService;

public class Translation {

    private final BasePlugin plugin;

    private final LocalizationService service;

    private String locale_ident;

    private String[] locale_args;

    public Translation(BasePlugin plugin, String locale_ident) {
        this(plugin, locale_ident, new String[]{});
    }

    public Translation(BasePlugin plugin, String locale_ident, String[] locale_args) {
        this.plugin     = plugin;
        this.service    = this.plugin.getLocalizationService();

        this.setLocale_ident(locale_ident);
        this.setLocale_args(locale_args);
    }

    public String getTranslation() {
        try {
            return this.service.getTranslation(this.getLocale_ident(), this.getLocale_args());
        } catch (LocalizationNotFoundException e) {
            return "";
        }
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
}
