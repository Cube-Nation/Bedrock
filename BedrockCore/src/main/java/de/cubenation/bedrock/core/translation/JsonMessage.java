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
import de.cubenation.bedrock.core.injection.Component;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public class JsonMessage extends Component {

    @Inject
    private LocalizationService localizationService;

    @Inject(from = "Bedrock")
    private LocalizationService fallbackLocalizationService;

    @Inject
    private ColorSchemeService colorSchemeService;

    @Getter @Setter
    private String localeIdentifier;

    @Getter
    private String json;

    @Getter
    private String[] localeArgs;

    public JsonMessage(FoundationPlugin plugin, String localeIdentifier) {
        this(plugin, localeIdentifier, new String[]{});
    }

    public JsonMessage(FoundationPlugin plugin, String localeIdentifier, String... localeArgs) {
        super(plugin);
        this.localeIdentifier = localeIdentifier;
        setLocaleArgs(localeArgs);

        json = getTranslation();
    }

    public JsonMessage(FoundationPlugin plugin, JSONObject bedrockJson) {
        super(plugin);

        json = bedrockJson.toJSONString();
    }

    public void send(BedrockChatSender commandSender) {
        send(commandSender, ChatMessageType.CHAT);
    }

    public void send(BedrockChatSender commandSender, ChatMessageType chatMessageType) {
        BaseComponent[] components = createBaseComponent();
        if (components == null) return;

        if (commandSender instanceof BedrockPlayer player) {
            sendPlayer(player, components, chatMessageType);
        } else {
            sendConsole(commandSender, components);
        }
    }

    public void broadcast() {
        broadcast(ChatMessageType.CHAT);
    }

    @SuppressWarnings("WeakerAccess")
    public void broadcast(ChatMessageType chatMessageType) {
        for (BedrockPlayer player : plugin.getBedrockServer().getOnlinePlayers()) {
            send(player, chatMessageType);
        }
    }

    public void broadcast(ArrayList<BedrockPlayer> withoutPlayer) {
        broadcast(withoutPlayer, ChatMessageType.CHAT);
    }

    @SuppressWarnings("WeakerAccess")
    public void broadcast(ArrayList<BedrockPlayer> withoutPlayer, ChatMessageType chatMessageType) {
        for (BedrockPlayer player : plugin.getBedrockServer().getOnlinePlayers()) {
            if (withoutPlayer.contains(player)) {
                continue;
            }
            send(player, chatMessageType);
        }
    }

    private void sendPlayer(BedrockPlayer player, BaseComponent[] components, ChatMessageType chatMessageType) {
        player.sendMessage(chatMessageType, components);
    }

    private void sendConsole(BedrockChatSender commandSender, BaseComponent[] components) {
        String legacyText = BaseComponent.toLegacyText(components);
        commandSender.sendMessage(legacyText);
    }

    private BaseComponent[] createBaseComponent() {
        // color scheme service
        ColorScheme colorScheme = colorSchemeService.getColorScheme();

        // apply colors from color scheme to message
        json = colorScheme.applyColorSchemeForJson(json);

        return ComponentSerializer.parse(json);
    }

    public String getPlainText() {
        BaseComponent[] components = createBaseComponent();
        if (components == null) {
            return "";
        }

        return BaseComponent.toPlainText(components);
    }

    public String getTranslation() {
        // try to get the localized string from the plugins locale file
        try {
            return localizationService.getTranslation(localeIdentifier, localeArgs);
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return fallbackLocalizationService.getTranslation(localeIdentifier, localeArgs);
            } catch (LocalizationNotFoundException ignored) {
            }
        }

        // we do not return null to avoid NullPointerExceptions.
        // If you see an empty string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return "";
    }

    public String[] getTranslationStrings() {
        // try to get the localized string from the plugins locale file
        try {
            return localizationService.getTranslationStrings(localeIdentifier, localeArgs);
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return fallbackLocalizationService.getTranslationStrings(localeIdentifier, localeArgs);
            } catch (LocalizationNotFoundException ignored) {
            }
        }

        // we do not return null to avoid NullPointerExceptions.
        // If you see an empty string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return new String[]{};
    }

    public void setLocaleArgs(String[] localeArgs) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(localeArgs));

        boolean customPrefix = false;
        for (String localeArg : localeArgs) {
            if (localeArg.equals("plugin_prefix")) {
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
        this.localeArgs = args.toArray(stringArgs);
    }
}

