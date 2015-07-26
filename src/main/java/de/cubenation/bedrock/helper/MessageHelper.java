package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.translation.Translation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageHelper {

    public static void commandExecutionError(BasePlugin plugin, CommandSender sender, Exception e) {
        MessageHelper.send(plugin, sender, plugin.getMessagePrefix() + " " + e.getMessage());
    }

    public static void insufficientPermission(BasePlugin plugin, CommandSender sender) {
        MessageHelper.send(
                plugin,
                sender,
                new Translation(BedrockPlugin.getInstance(), "permission.insufficient").getTranslation()
        );
    }

    public static void invalidCommand(BasePlugin plugin, CommandSender sender) {
        MessageHelper.send(
                plugin,
                sender,
                plugin.getMessagePrefix() + " " +
                        new Translation(BedrockPlugin.getInstance(), "command.invalid").getTranslation()
        );
    }

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

        // apply colors from color scheme to message
        message = plugin.getColorScheme().applyColorScheme(message);

        if (sender instanceof Player) {
            if (hover_event != null)
                message.setHoverEvent(plugin.getColorScheme().applyColorScheme(hover_event));
            if (click_event != null)
                message.setClickEvent(plugin.getColorScheme().applyColorScheme(click_event));

            ((Player) sender).spigot().sendMessage(message);

        } else {
            String hover_message = "";
            // ClickEvents are not supported here
            //String click_message = "";

            if (hover_event != null) {
                hover_event = plugin.getColorScheme().applyColorScheme(hover_event);

                for (int i = 0; i < hover_event.getValue().length; i++) {
                    hover_message += hover_event.getValue()[i].toLegacyText();
                }

                // check for multiline string
                if (hover_message.contains("\n")) {

                    ArrayList<String> lines = new ArrayList<>();
                    lines.addAll(Arrays.asList(hover_message.split("\\r?\\n")));

                    int max_len = longestLength(lines);

                    // generate header and footer line
                    String hf_line =
                            ChatColor.DARK_GRAY + " " +
                            new String(new char[max_len + 4]).replace('\0', '-') +
                            ChatColor.RESET;
                    ;
                    lines.add(0, hf_line);

                    for (int i = 1; i < lines.size(); i++) {
                        int pad_length = max_len - ChatColor.stripColor(lines.get(i)).length();
                        lines.set(i,
                                ChatColor.DARK_GRAY + "  | " + ChatColor.RESET +
                                lines.get(i) +
                                new String(new char[pad_length]).replace('\0', ' ') +
                                ChatColor.DARK_GRAY + " |" + ChatColor.RESET
                        );
                    }

                    lines.add(hf_line);
                    hover_message = "\n" + StringUtils.join(lines, System.lineSeparator());

                } else {
                    hover_message =
                            " " +   // make explicit string
                            ChatColor.DARK_GRAY + "(" + ChatColor.RESET +
                            hover_message +
                            ChatColor.DARK_GRAY + ")" + ChatColor.RESET;
                }
            }

            // finally send message
            sender.sendMessage(message.toLegacyText() + hover_message);
        }
    }

    private static int longestLength(ArrayList<String> list){
        String longest = list.get(0);
        for(String str : list){
            if(str.length()> longest.length()){
                longest = str;
            }
        }
        return longest.length();
    }

}