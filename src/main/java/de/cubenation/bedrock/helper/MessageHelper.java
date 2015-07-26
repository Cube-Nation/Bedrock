package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageHelper {

    protected static void send(BasePlugin plugin, CommandSender sender, TextComponent message, HoverEvent hover_event, ClickEvent click_event) {
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
}
