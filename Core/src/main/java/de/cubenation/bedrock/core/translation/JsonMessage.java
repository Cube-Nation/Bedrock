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
import de.cubenation.bedrock.core.command.BedrockCommandSender;
import de.cubenation.bedrock.core.command.BedrockPlayerCommandSender;
import de.cubenation.bedrock.core.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.core.service.localization.LocalizationService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Cube-Nation
 * @version 1.0
 */

@SuppressWarnings("unused")
public class JsonMessage {

    private final FoundationPlugin plugin;

    private String localeIdentifier;

    private String json;

    private String[] localeArgs;

    private final LocalizationService service;

    public JsonMessage(FoundationPlugin plugin, String localeIdentifier) {
        this(plugin, localeIdentifier, new String[]{});
    }

    public JsonMessage(FoundationPlugin plugin, String localeIdentifier, String... localeArgs) {
        this.plugin = plugin;
        this.localeIdentifier = localeIdentifier;
        this.setLocaleArgs(localeArgs);
        this.service = plugin.getLocalizationService();

        json = getTranslation();
    }

    public JsonMessage(FoundationPlugin plugin, JSONObject bedrockJson) {
        this.plugin = plugin;
        this.service = plugin.getLocalizationService();

        json = bedrockJson.toJSONString();
    }

    public void send(BedrockCommandSender commandSender) {
        send(commandSender, ChatMessageType.CHAT);
    }

    public void send(BedrockCommandSender commandSender, ChatMessageType chatMessageType) {
        BaseComponent[] components = createBaseComponent();
        if (components == null) return;

        if (commandSender instanceof BedrockPlayerCommandSender) {
            BedrockPlayerCommandSender player = (BedrockPlayerCommandSender) commandSender;
            sendPlayer(player, components, chatMessageType);
        } else {
            sendConsole(commandSender, components);
        }
    }

    public void broadcast() {
        broadcast(ChatMessageType.CHAT);
    }

    // TODO: tbd
    public void broadcast(ChatMessageType chatMessageType) {
//        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//            send(player, chatMessageType);
//        }
    }

    public void broadcast(ArrayList<BedrockPlayerCommandSender> withoutPlayer) {
        broadcast(withoutPlayer, ChatMessageType.CHAT);
    }

    // TODO: tbd
    public void broadcast(ArrayList<BedrockPlayerCommandSender> withoutPlayer, ChatMessageType chatMessageType) {
//        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//            if (withoutPlayer.contains(player)) {
//                continue;
//            }
//            send(player, chatMessageType);
//        }
    }

    private void sendPlayer(BedrockPlayerCommandSender player, BaseComponent[] components, ChatMessageType chatMessageType) {
        player.sendMessage(chatMessageType, components);
    }

    private void sendConsole(BedrockCommandSender commandSender, BaseComponent[] components) {
        String legacyText = BaseComponent.toLegacyText(components);
        commandSender.sendMessage(legacyText);
    }

    private BaseComponent[] createBaseComponent() {
        // color scheme service
        ColorScheme colorScheme = plugin.getColorSchemeService().getColorScheme();

        // apply colors from color scheme to message
        json = colorScheme.applyColorSchemeForJson(json);

        BaseComponent[] components = ComponentSerializer.parse(json);
        if (components == null) {
            return null;
        }
        return components;
    }

    public String getTranslation() {
        // try to get the localized string from the plugins locale file
        try {
            return this.service.getTranslation(this.localeIdentifier, this.localeArgs);
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return plugin.getFallbackBedrockPlugin().getLocalizationService().getTranslation(
                        this.localeIdentifier, this.localeArgs
                );
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
            return this.service.getTranslationStrings(this.localeIdentifier, this.localeArgs);
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return plugin.getFallbackBedrockPlugin().getLocalizationService().getTranslationStrings(
                        this.localeIdentifier, this.localeArgs
                );
            } catch (LocalizationNotFoundException ignored) {
            }
        }

        // we do not return null to avoid NullPointerExceptions.
        // If you see an empty string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return new String[]{};
    }


    public FoundationPlugin getPlugin() {
        return plugin;
    }

    public String getJson() {
        return json;
    }

    public String getLocaleIdentifier() {
        return localeIdentifier;
    }

    public String[] getLocaleArgs() {
        return localeArgs;
    }

    public LocalizationService getService() {
        return service;
    }

    public void setLocaleIdentifier(String localeIdentifier) {
        this.localeIdentifier = localeIdentifier;
    }

    public void setLocaleArgs(String[] localeArgs) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(localeArgs));

        boolean custom_prefix = false;
        for (String localeArg : localeArgs) {
            if (localeArg.equals("plugin_prefix")) {
                custom_prefix = true;
                break;
            }
        }

        if (!custom_prefix) {
            args.add("plugin_prefix");
            args.add(this.getPlugin().getMessagePrefix());
        }

        // cast back to String[]
        String[] string_args = new String[args.size()];
        localeArgs = args.toArray(string_args);
        this.localeArgs = localeArgs;
    }
}

