package de.cubenation.bedrock.translation;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.service.localization.LocalizationService;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by BenediktHr on 19.02.16.
 * Project: Bedrock
 */

@SuppressWarnings("unused")
public class JsonMessage {

    private final BasePlugin plugin;

    private String localeIdentifier;

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
    }

    public void send(CommandSender commandSender) {
        String jsonMessage = getTranslation();
        if (jsonMessage == null) {
            return;
        }

        // color scheme service
        ColorScheme colorScheme = plugin.getColorSchemeService().getColorScheme();

        // apply colors from color scheme to message
        jsonMessage = colorScheme.applyColorSchemeForJson(jsonMessage);

        BaseComponent[] components = ComponentSerializer.parse(jsonMessage);
        if (components == null) {
            return;
        }

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            sendPlayer(player, components);
        } else {
            sendConsole(commandSender, components);
        }
    }

    private void sendPlayer(Player player, BaseComponent[] components) {
        player.spigot().sendMessage(components);
    }

    private void sendConsole(CommandSender commandSender, BaseComponent[] components) {
        String legacyText = BaseComponent.toLegacyText(components);
        commandSender.sendMessage(legacyText);
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
