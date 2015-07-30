package de.cubenation.bedrock.translation;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.service.localization.LocalizationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class Translation {

    private BasePlugin plugin;

    private String locale_ident;

    private String[] locale_args;

    private final LocalizationService service;


    public Translation(BasePlugin plugin, String locale_ident) {
        this(plugin, locale_ident, new String[]{});
    }

    public Translation(BasePlugin plugin, String locale_ident, String[] locale_args) {
        if (plugin instanceof BedrockPlugin) {
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            plugin.log(Level.WARNING, "========================================================================================================");
            plugin.log(Level.WARNING, "By passing the BedrockPlugin instance to the constructor of the Translation class");
            plugin.log(Level.WARNING, "locale strings from your plugin cannot be resolved and will only be taken from the Bedrocks' locale file");
            plugin.log(Level.WARNING, "Your call came from:");
            for (int i = 0; i < stackTraces.length; i++) {
                plugin.log(Level.WARNING, stackTraces[i].getFileName() + ": " + stackTraces[i].getClassName() + ":" + stackTraces[i].getMethodName() + " line " + stackTraces[i].getLineNumber());
            }
            plugin.log(Level.WARNING, "========================================================================================================");
        }

        this.setPlugin(plugin);
        this.setLocale_ident(locale_ident);
        this.setLocale_args(locale_args);

        this.service    = plugin.getLocalizationService();
    }


    private BasePlugin getPlugin() {
        return plugin;
    }

    private void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
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

    private void setLocale_args(String[] my_locale_args) {
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(my_locale_args));
        args.add("plugin_prefix");
        args.add(this.getPlugin().getMessagePrefix());

        // cast back to String[]
        String[] string_args = new String[args.size()];
        my_locale_args = args.toArray(string_args);
        this.locale_args = my_locale_args;
    }


    public String getTranslation() {
        // try to get the localized string from the plugins locale file
        try {
            return this.service.getTranslation(this.getLocale_ident(), this.getLocale_args());
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!(this.plugin instanceof BedrockPlugin)) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return BedrockPlugin.getInstance().getLocalizationService().getTranslation(
                        this.getLocale_ident(), this.getLocale_args()
                );
            } catch (LocalizationNotFoundException ignored) {
            }
        }

        // we do not return null to aboid NullPointerExceptions.
        // If you see an empty string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return "";
    }

}
