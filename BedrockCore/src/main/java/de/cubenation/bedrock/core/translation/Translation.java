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

package de.cubenation.bedrock.core.translation;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.core.injection.Component;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class Translation extends Component {

    @Inject
    private LocalizationService localizationService;

    @Inject(from = "Bedrock")
    private LocalizationService fallbackLocalizationService;

    @Inject
    private ColorSchemeService colorSchemeService;

    @Getter @Setter
    private String localeIdent;

    @Getter
    private String[] localeArgs;


    public Translation(FoundationPlugin plugin, String localeIdent) {
        this(plugin, localeIdent, new String[]{});
    }

    public Translation(FoundationPlugin plugin, String localeIdent, String[] localeArgs) {
        super(plugin);
        /*
        if (plugin instanceof BedrockPlugin) {
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            plugin.log(Level.WARNING, "========================================================================================================");
            plugin.log(Level.WARNING, "By passing the BedrockPlugin instance to the constructor of the Translation class");
            plugin.log(Level.WARNING, "locale strings from your plugin cannot be resolved and will only be taken from the Bedrocks' locale file");
            plugin.log(Level.WARNING, "Your call came from:");
            for (StackTraceElement stackTrace : stackTraces) {
                plugin.log(Level.WARNING, stackTrace.getFileName() + ": " + stackTrace.getClassName() + ":" + stackTrace.getMethodName() + " line " + stackTrace.getLineNumber());
            }
            plugin.log(Level.WARNING, "========================================================================================================");
        }
        */

        setLocaleIdent(localeIdent);
        setLocaleArgs(localeArgs);
    }

    public void send(BedrockChatSender commandSender) {
        String message = getTranslation();
        if (message == null) {
            return;
        }

        // color scheme service
        ColorScheme colorScheme = colorSchemeService.getColorScheme();

        // apply colors from color scheme to message
        message = colorScheme.applyColorScheme(message);
        TextComponent component = new TextComponent(message);

        if (commandSender instanceof BedrockPlayer player) {
            sendPlayer(player, component);
        } else {
            sendConsole(commandSender, component);
        }
    }

    public void broadcast() {
        for (BedrockPlayer player : plugin.getBedrockServer().getOnlinePlayers()) {
            send(player);
        }
    }

    private void sendPlayer(BedrockPlayer player, TextComponent components) {
        player.sendMessage(components);
    }

    private void sendConsole(BedrockChatSender commandSender, TextComponent components) {
        String legacyText = BaseComponent.toLegacyText(components);
        commandSender.sendMessage(legacyText);
    }

    private void setLocaleArgs(String[] myLocaleArgs) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(myLocaleArgs));

        boolean customPrefix = false;
        for (String my_locale_arg : myLocaleArgs) {
            if (my_locale_arg.equals("plugin_prefix")) {
                customPrefix = true;
                break;
            }
        }

        if (!customPrefix) {
            args.add("plugin_prefix");
            args.add(plugin.getMessagePrefix());
        }

        // cast back to String[]
        String[] stringArgs = new String[args.size()];
        myLocaleArgs = args.toArray(stringArgs);
        localeArgs = myLocaleArgs;
    }


    public String getTranslation() {
        // try to get the localized string from the plugins locale file
        try {
            return localizationService.getTranslation(getLocaleIdent(), getLocaleArgs());
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return fallbackLocalizationService.getTranslation(getLocaleIdent(), getLocaleArgs());
            } catch (LocalizationNotFoundException ignored) {
            }
        }

        // we do not return null to avoid NullPointerExceptions.
        // If you see a not translated string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return localeIdent;
    }

    public TextComponent getTextComponent() {
        return new TextComponent(getTranslation());
    }

    public String[] getTranslationStrings() {
        // try to get the localized string from the plugins locale file
        try {
            return localizationService.getTranslationStrings(getLocaleIdent(), getLocaleArgs());
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return fallbackLocalizationService.getTranslationStrings(getLocaleIdent(), getLocaleArgs());
            } catch (LocalizationNotFoundException ignored) {
            }
        }

        // we do not return null to avoid NullPointerExceptions.
        // If you see an empty string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return new String[]{};
    }

}

