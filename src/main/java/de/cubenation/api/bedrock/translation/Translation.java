package de.cubenation.api.bedrock.translation;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.BedrockPlugin;
import de.cubenation.api.bedrock.exception.LocalizationNotFoundException;
import de.cubenation.api.bedrock.service.colorscheme.ColorScheme;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Translation {

    private BasePlugin plugin;

    private String locale_ident;

    private String[] locale_args;

    private final LocalizationService service;


    public Translation(BasePlugin plugin, String locale_ident) {
        this(plugin, locale_ident, new String[]{});
    }

    public Translation(BasePlugin plugin, String locale_ident, String[] locale_args) {
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

        this.setPlugin(plugin);
        this.setLocale_ident(locale_ident);
        this.setLocale_args(locale_args);

        this.service    = plugin.getLocalizationService();
    }

    public void send(CommandSender commandSender) {
        String message = getTranslation();
        if (message == null) {
            return;
        }

        // color scheme service
        ColorScheme colorScheme = plugin.getColorSchemeService().getColorScheme();

        // apply colors from color scheme to message
        message = colorScheme.applyColorScheme(message);
        TextComponent component = new TextComponent(message);

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            sendPlayer(player, component);
        } else {
            sendConsole(commandSender, component);
        }
    }

    public void broadcast() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            send(player);
        }
    }

    private void sendPlayer(Player player, TextComponent components) {
        player.spigot().sendMessage(components);
    }

    private void sendConsole(CommandSender commandSender, TextComponent components) {
        String legacyText = BaseComponent.toLegacyText(components);
        commandSender.sendMessage(legacyText);
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
        ArrayList<String> args = new ArrayList<>(Arrays.asList(my_locale_args));

        boolean custom_prefix = false;
        for (String my_locale_arg : my_locale_args) {
            if (my_locale_arg.equals("plugin_prefix")) {
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

        // we do not return null to avoid NullPointerExceptions.
        // If you see an empty string somewhere
        //  a) the locale file is damaged/incomplete - try deleting it and restart the server
        //  b) check if the plugin refers to the correct path in the YamlConfiguration object
        return "";
    }

    public TextComponent getTextComponent() {
        return new TextComponent(getTranslation());
    }

    public String[] getTranslationStrings() {
        // try to get the localized string from the plugins locale file
        try {
            return this.service.getTranslationStrings(this.getLocale_ident(), this.getLocale_args());
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!(this.plugin instanceof BedrockPlugin)) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return BedrockPlugin.getInstance().getLocalizationService().getTranslationStrings(
                        this.getLocale_ident(), this.getLocale_args()
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

}