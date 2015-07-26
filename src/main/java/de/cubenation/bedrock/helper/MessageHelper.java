package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHelper {

    public static void send(BasePlugin plugin, CommandSender sender, String message) {
        send(plugin, sender, new TextComponent(message));
    }

    public static void send(BasePlugin plugin, CommandSender sender, TextComponent component) {
        send(plugin, sender, component, component.getHoverEvent(), component.getClickEvent());
    }

    public static void send(BasePlugin plugin, CommandSender sender, TextComponent message, HoverEvent hover_event, ClickEvent click_event) {
        // check for NullPointerException
        if (message == null)
            return;

        if (sender instanceof Player) {
            if (hover_event != null)
                message.setHoverEvent(hover_event);
            if (click_event != null)
                message.setClickEvent(click_event);

            ((Player) sender).spigot().sendMessage(message);

        } else {
            String hover_message = "";

            if (hover_event != null) {
                hover_message = plugin.getFlagColor() + " (";
                for (int i = 0; i < hover_event.getValue().length; i++) {
                    hover_message += ChatColor.translateAlternateColorCodes('&', hover_event.getValue()[i].toLegacyText());
                }
                hover_message += plugin.getFlagColor() + ")";
            }

            sender.sendMessage(message.toPlainText() + hover_message);
        }
    }

    @SuppressWarnings("unused")
    private static String applyColors(BasePlugin plugin, String s) {
        String regex =
                "(&" +
                        "(" +
                        "BLACK|DARK_BLUE|DARK_GREEN|DARK_AQUA|DARK_RED|DARK_PURPLE|GOLD|GRAY|DARK_GRAY|BLUE|GREEN|AQUA|RED|LIGHT_PURPLE|YELLOW|WHITE" +
                        "|" +
                        "STRIKETHROUGH|UNDERLINE|BOLD|MAGIC|ITALIC|RESET" +
                        "|" +
                        "PRIMARY|SECONDARY|FLAG" +
                        ")" +
                "&)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            if (matcher.group(2).equals("PRIMARY")) {
                matcher.appendReplacement(sb, "" + plugin.getPrimaryColor());
            } else if (matcher.group(2).equals("SECONDARY")) {
                matcher.appendReplacement(sb, "" + plugin.getSecondaryColor());
            } else if (matcher.group(2).equals("FLAG")) {
                matcher.appendReplacement(sb, "" + plugin.getFlagColor());
            } else {
                matcher.appendReplacement(sb, ChatColor.valueOf(matcher.group(2)).toString());
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
