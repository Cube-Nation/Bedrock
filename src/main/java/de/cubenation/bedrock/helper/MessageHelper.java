package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.argument.CommandArguments;
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
                            ChatColor.DARK_GRAY + "   " +
                            new String(new char[max_len + 3]).replace('\0', '-') +
                            ChatColor.RESET;

                    lines.add(0, hf_line);

                    for (int i = 1; i < lines.size(); i++) {
                        int pad_length = max_len - ChatColor.stripColor(lines.get(i)).length();
                        lines.set(i,
                                ChatColor.DARK_GRAY + "  | " + ChatColor.RESET +
                                lines.get(i) +
                                new String(new char[pad_length]).replace('\0', ' ') +
                                ChatColor.DARK_GRAY + "  |" + ChatColor.RESET
                        );
                    }

                    lines.add(hf_line);
                    hover_message = "\n" + StringUtils.join(lines, System.lineSeparator());

                } else {
                    hover_message =
                            " " +   // make explicit string
                            ChatColor.DARK_GRAY + "[ " + ChatColor.RESET +
                            hover_message +
                            ChatColor.DARK_GRAY + " ]" + ChatColor.RESET;
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


    /**
     * Get a TextComponent with the help for a SubCommand
     *
     * @param plugin      a reference to the plugin
     * @param sender      the command sender
     * @param command     a class that is abstracted from AbstractCommand
     * @return the TextComponent with the help.
     */
    public static TextComponent getHelpForSubCommand(BasePlugin plugin, CommandSender sender, AbstractCommand command) {

        // check for permission
        if (!command.hasPermission(sender))
            return null;

        // clickable string
        ArrayList<String> click_string = new ArrayList<>();
        click_string.add("/" + command.getLabel());

        // command divider string
        String command_divider = new Translation(
                BedrockPlugin.getInstance(),
                "help.command.divider"
        ).getTranslation();

        // command string
        ArrayList<String> command_strings = new ArrayList<>();
        if (command.getCommands() != null) {
            for (String[] commands : command.getCommands()) {
                Arrays.sort(commands, new LengthComparator());
                command_strings.add(StringUtils.join(commands, command_divider));

                click_string.add((commands.length > 1) ? commands[1] : commands[0]);
            }
        }

        // description for command
        String description_string = "";

        if (command.getDescription() != null && !command.getDescription().isEmpty()) {
            description_string = new Translation(plugin, command.getDescription()).getTranslation();
            if (description_string.isEmpty())
                description_string = new Translation(BedrockPlugin.getInstance(), command.getDescription()).getTranslation();
        }



        // command args string (
        ArrayList<String> args_string = new ArrayList<>();
        ArrayList<String> long_args_string = new ArrayList<>();

        CommandArguments command_arguments = command.getCommandArguments();
        // process arguments
        for (int i = 0; i < command_arguments.size(); i++) {
            Argument argument = command_arguments.get(i);

            // placeholders
            String placeholder;
            if (argument.isOptional()) {
                placeholder = new Translation(
                        BedrockPlugin.getInstance(),
                        "help.command.args.optional",
                        new String[] { "argument", StringUtils.join(argument.getPlaceholder(), "/") }
                ).getTranslation();

            } else {
                placeholder = new Translation(
                        BedrockPlugin.getInstance(),
                        "help.command.args.needed",
                        new String[] { "argument", StringUtils.join(argument.getPlaceholder(), "/") }
                ).getTranslation();

            }

            // description for argument from BedrockPlugin
            String description = new Translation(
                    BedrockPlugin.getInstance(),
                    "help.command.args.description",
                    new String[] { "description", argument.getDescription() }
            ).getTranslation();

            // description for argument from our plugin in if was empty
            if (description.isEmpty())
                description = new Translation(
                        plugin,
                        "help.command.args.description",
                        new String[] { "description", argument.getDescription() }
                ).getTranslation();

            args_string.add(placeholder);
            long_args_string.add(placeholder + " " + description);
        }

        // finally
        String help_string = new Translation(
                BedrockPlugin.getInstance(),
                "help.command.command",
                new String[]{
                        "label",    command.getLabel(),
                        "commands", StringUtils.join(command_strings, " "),
                        "args",     StringUtils.join(args_string, " ")
                }
        ).getTranslation();

        String hover_string = description_string;
        if (!long_args_string.isEmpty())
            hover_string += System.lineSeparator() + StringUtils.join(long_args_string, System.lineSeparator());

        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(help_string)
        );
        component.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                StringUtils.join(click_string, " ")
        ));
        component.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(hover_string)
        ));

        return component;
    }

}
