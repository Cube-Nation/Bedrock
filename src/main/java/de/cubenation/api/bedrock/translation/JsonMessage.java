package de.cubenation.api.bedrock.translation;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import de.cubenation.plugin.bedrock.BedrockPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Cube-Nation
 * @version 1.0
 */

@SuppressWarnings("unused")
public class JsonMessage {

    private final BasePlugin plugin;

    private String localeIdentifier;

    private String json;

    private String[] localeArgs;

    private final LocalizationService service;

    public JsonMessage(BasePlugin plugin, String localeIdentifier) {
        this(plugin, localeIdentifier, new String[]{});
    }

    public JsonMessage(BasePlugin plugin, String localeIdentifier, String... localeArgs) {
        this.plugin = plugin;
        this.localeIdentifier = localeIdentifier;
        this.setLocaleArgs(localeArgs);
        this.service = plugin.getLocalizationService();

        json = getTranslation();
    }

    public JsonMessage(BasePlugin plugin, JSONObject bedrockJson) {
        this.plugin = plugin;
        this.service = plugin.getLocalizationService();

        json = bedrockJson.toJSONString();
    }

    public void send(CommandSender commandSender) {
        send(commandSender, ChatMessageType.CHAT);
    }

    public void send(CommandSender commandSender, ChatMessageType chatMessageType) {
        BaseComponent[] components = createBaseComponent();
        if (components == null) return;

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            sendPlayer(player, components, chatMessageType);
        } else {
            sendConsole(commandSender, components);
        }
    }

    public void broadcast() {
        broadcast(ChatMessageType.CHAT);
    }

    public void broadcast(ChatMessageType chatMessageType) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            send(player, chatMessageType);
        }
    }

    public void broadcast(ArrayList<Player> withoutPlayer) {
        broadcast(withoutPlayer, ChatMessageType.CHAT);
    }

    public void broadcast(ArrayList<Player> withoutPlayer, ChatMessageType chatMessageType) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (withoutPlayer.contains(player)) {
                continue;
            }
            send(player, chatMessageType);
        }
    }

    private void sendPlayer(Player player, BaseComponent[] components, ChatMessageType chatMessageType) {
        player.spigot().sendMessage(chatMessageType, components);
    }

    private void sendConsole(CommandSender commandSender, BaseComponent[] components) {
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

        if (!(this.plugin instanceof BedrockPlugin)) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return BedrockPlugin.getInstance().getLocalizationService().getTranslation(
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

        if (!(this.plugin instanceof BedrockPlugin)) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return BedrockPlugin.getInstance().getLocalizationService().getTranslationStrings(
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


    public BasePlugin getPlugin() {
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
