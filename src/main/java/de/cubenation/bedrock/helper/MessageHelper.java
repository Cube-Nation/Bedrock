package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.AbstractCommand;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @SuppressWarnings("unused")
    private static String applyColors(BasePlugin plugin, String s) {
        String regex =
                "(&" +
                        "(" +
                        "BLACK|DARK_BLUE|DARK_GREEN|DARK_AQUA|DARK_RED|DARK_PURPLE|GOLD|GRAY|DARK_GRAY|BLUE|GREEN|AQUA|RED|LIGHT_PURPLE|YELLOW|WHITE" +
                        "|" +
                        "STRIKETHROUGH|UNDERLINE|BOLD|MAGIC|ITALIC|RESET" +
                        "|" +
                        "PRIMARY|SECONDARY|FLAG|TEXT" +
                        ")" +
                "&)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            if (matcher.group(2).equals("PRIMARY")) {
                matcher.appendReplacement(sb, "" + plugin.getColorScheme().getPrimary());
            } else if (matcher.group(2).equals("SECONDARY")) {
                matcher.appendReplacement(sb, "" + plugin.getColorScheme().getSecondary());
            } else if (matcher.group(2).equals("FLAG")) {
                matcher.appendReplacement(sb, "" + plugin.getColorScheme().getFlag());
            } else if (matcher.group(2).equals("TEXT")) {
                matcher.appendReplacement(sb, "" + plugin.getColorScheme().getText());
            } else {
                matcher.appendReplacement(sb, ChatColor.valueOf(matcher.group(2)).toString());
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }


    //TODO Update Javadoc
    /**
     * Get a TextComponent with the help for a SubCommand
     *
//     * @param com the SubCommand
//     * @param sender     the command sender
//     * @param label      the label of the command
     * @return the TextComponent with the help.
     */
    public static TextComponent getHelpForSubCommand(BasePlugin plugin, AbstractCommand command) {

        //Different Help for each Arguments

//FIXME D1rty
//        if (!command.hasPermission(sender)) {
//            return null;
//        }
//
//        ChatColor primary = plugin.getColorScheme().getPrimary();
//        ChatColor secondary = plugin.getColorScheme().getSecondary();
//
//        String cmd = primary + "/" + label + "" +
//                secondary;
//        String useCommand = cmd;
//        if (command.getCommands() != null) {
//            for (String[] commands : command.getCommands()) {
//                Arrays.sort(commands, new LengthComparator());
//                cmd += " " + StringUtils.join(commands, primary + "|" + secondary);
//                useCommand += " " + commands[0];
//            }
//        }
//
//        String cmdWithArgument = cmd;
//
//        String toolTipHelp = "";
//        for (String helpString : command.getHelp()) {
//            toolTipHelp += System.lineSeparator() + ChatColor.WHITE + helpString;
//        }
//
//        if (command.getArguments() != null) {
//            for (Map.Entry<String, String> entry : command.getArguments().entrySet()) {
//                cmdWithArgument += " " + entry.getKey();
//                toolTipHelp += System.lineSeparator() +
//                        ChatColor.GRAY + ChatColor.ITALIC + entry.getKey() +
//                        ChatColor.RESET;
//
//                if (entry.getValue() != null)
//                    toolTipHelp += " - " + entry.getValue();
//                // FIXME: somehow the reset does not work -> looks ugly in Console
//                //toolTipHelp += ChatColor.ITALIC + " - " + entry.getValue() + ChatColor.RESET;
//
//            }
//        }
//
//        String help = secondary + "" + cmdWithArgument + toolTipHelp;
//
//        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(cmdWithArgument));
//        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(useCommand)));
//        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(help)));

        return new TextComponent(command.getCommands().get(0)[0] + " : Fix Me\n");
    }
}
