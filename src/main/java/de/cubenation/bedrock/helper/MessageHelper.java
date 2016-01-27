package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.argument.UnsortedArgument;
import de.cubenation.bedrock.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.translation.Translation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
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
                new Translation(plugin, "permission.insufficient").getTranslation()
        );
    }

    public static void invalidCommand(BasePlugin plugin, CommandSender sender) {
        MessageHelper.send(
                plugin,
                sender,
                new Translation(plugin, "command.invalid").getTranslation()
        );
    }

    @SuppressWarnings("unused")
    public static void mustBePlayer(BasePlugin plugin, CommandSender commandSender) {
        send(plugin, commandSender, new Translation(plugin, "must_be_player").getTranslation());
    }

    public static void noSuchPlayer(BasePlugin plugin, CommandSender commandSender, String player) {
        send(plugin, commandSender, new Translation(
                plugin, "no_such_player",
                new String[]{"player", player}
        ).getTranslation());
    }

    @SuppressWarnings("unused")
    public static void sendToAll(BasePlugin plugin, String message) {
        sendToAll(plugin, new TextComponent(message));
    }

    public static void send(BasePlugin plugin, CommandSender sender, String message) {
        send(plugin, sender, new TextComponent(message));
    }

    public static void sendToAll(BasePlugin plugin, TextComponent component) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            send(plugin, player, component);
        }
    }

    public static void send(BasePlugin plugin, CommandSender sender, TextComponent component) {
        // check for NullPointerException
        if (component == null)
            return;
        send(plugin, sender, component, component.getHoverEvent(), component.getClickEvent());
    }

    public static void send(BasePlugin plugin, CommandSender sender, TextComponent component, HoverEvent hover_event, ClickEvent click_event) {
        // check for NullPointerException
        if (component == null)
            return;

        // color scheme service
        ColorScheme color_scheme = plugin.getColorSchemeService().getColorScheme();

        // apply colors from color scheme to message
        component = color_scheme.applyColorScheme(component);

        if (sender instanceof Player) {
            if (hover_event != null)
                component.setHoverEvent(color_scheme.applyColorScheme(hover_event));
            if (click_event != null)
                component.setClickEvent(color_scheme.applyColorScheme(click_event));

            ((Player) sender).spigot().sendMessage(component);

        } else {
            String hover_message = "";
            // ClickEvents are not supported here
            //String click_message = "";

            if (hover_event != null) {
                hover_event = color_scheme.applyColorScheme(hover_event);

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
            sender.sendMessage(component.toLegacyText() + hover_message);
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

        /*
         * suggest string
         *
         * This string contains the label and all subcommands (except arguments)
         * In case of a KeyValueCommand, the key-value arguments are not added to the clickable string
         *
         * The string is used for the suggest event when clicking on the help command output
         */
        ArrayList<String> suggest_string = new ArrayList<>();
        suggest_string.add("/" + command.getCommandManager().getPluginCommand().getLabel());


        // The command divider string
        String command_divider = new Translation(
                plugin,
                "help.command.divider"
        ).getTranslation();


        /*
         * command string
         *
         * Holds all subcommands for this command
         */
        ArrayList<String> command_strings = new ArrayList<>();
        if (command.getSubcommands() != null) {
            for (String[] commands : command.getSubcommands()) {
                // sort commands by length
                Arrays.sort(commands, new LengthComparator());

                // add command to command array
                command_strings.add(StringUtils.join(commands, command_divider));

                // add longest command to suggest string
                suggest_string.add((commands.length > 1) ? commands[1] : commands[0]);
            }
        }

        /*
         * Description for this command (label)
         *
         * The translation for this description string is taken from the plugin
         * or the BedrockPlugins' locale files
         */
        String command_description = "";

        if (command.getDescription() != null && !command.getDescription().isEmpty()) {
            command_description = new Translation(plugin, command.getDescription()).getTranslation();
        }


        /*
         * Command arguments and hover strings
         *
         * Command arguments string:
         * The string is being displayed in the command help which is printed into the chat.
         * It contains the label, all subcommands and all arguments (those from the UnsortedArgument, too)
         *
         * Hover string:
         * The hover string contains a linefeed-separated string of
         *  - the command description
         *     and linefeed-separated occurrences of all
         *  - command arguments and their description
         */
        ArrayList<String> argument_string   = new ArrayList<>();
        ArrayList<String> hover_string      = new ArrayList<>();
        // add the command description at first position
        hover_string.add(command_description);

        // process all arguments
        for (Argument argument : command.getArguments()) {

            if (!argument.userHasPermission(sender)) {
                continue;
            }

            ArrayList<String> argument_hover_string = new ArrayList<>();

            /*
             * UnsortedArgument
             *
             * In case the argument is an instanceof the UnsortedArgument class (which is kind of a
             * key-value command) we need to prepend the key
             */
            if (argument instanceof UnsortedArgument) {
                UnsortedArgument unsorted_argument = (UnsortedArgument) argument;

                String key_string = new Translation(
                        plugin,
                        "help.command.args.key",
                        new String[ ] {"key", unsorted_argument.getKey() }
                ).getTranslation();

                // add this to argument_hover_string
                argument_hover_string.add(key_string);
            }


            /*
             * Argument placeholder
             *
             *
             */
            // placeholders
            String placeholder;
            if (argument.isOptional()) {
                placeholder = new Translation(
                        plugin,
                        "help.command.args.optional",
                        new String[] { "argument", argument.getRuntimePlaceholder() }
                ).getTranslation();
            } else {
                placeholder = new Translation(
                        plugin,
                        "help.command.args.needed",
                        new String[] { "argument", argument.getRuntimePlaceholder() }
                ).getTranslation();
            }

            // add the placeholder to argument_hover_string
            argument_hover_string.add(placeholder);


            // add the arguments to argument_string. This does not contain the description
            argument_string.add(StringUtils.join(argument_hover_string, " "));


            // description for argument
            String argument_description = new Translation(
                    plugin,
                    "help.command.args.description",
                    new String[]{"description", argument.getRuntimeDescription()}
            ).getTranslation();

            //argument_hover_string.add(argument_description);
            // Do not add the argument_description to argument_hover_string.
            // This would lead to double whitespaces
            //
            // Instead, we just add the string after joining the argument_hover_string

            // finally add argument_hover_string to hover_string
            hover_string.add(StringUtils.join(argument_hover_string, " ") + argument_description);
        }

        // finally
        String help_string = new Translation(
                plugin,
                "help.command.command",
                new String[]{
                        "label",    command.getCommandManager().getPluginCommand().getLabel(),
                        "commands", StringUtils.join(command_strings, " "),
                        "args",     StringUtils.join(argument_string, " ")
                }
        ).getTranslation();


        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(help_string)
        );
        component.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                StringUtils.join(suggest_string, " ")
        ));
        component.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(StringUtils.join(hover_string, System.lineSeparator()))
        ));

        return component;
    }

}
