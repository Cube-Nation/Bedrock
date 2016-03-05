package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.argument.KeyValueArgument;
import de.cubenation.bedrock.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.translation.JsonMessage;
import de.cubenation.bedrock.translation.Translation;
import de.cubenation.bedrock.translation.parts.BedrockClickEvent;
import de.cubenation.bedrock.translation.parts.BedrockHoverEvent;
import de.cubenation.bedrock.translation.parts.BedrockJson;
import de.cubenation.bedrock.translation.parts.JsonColor;
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
        sender.sendMessage(plugin.getMessagePrefix() + " " + e.getMessage());
    }

    public static void insufficientPermission(BasePlugin plugin, CommandSender sender) {
        new JsonMessage(plugin, "json.permission.insufficient").send(sender);
    }

    public static void noPermission(BasePlugin plugin, CommandSender sender) {
        new JsonMessage(plugin, "json.no_permissions").send(sender);
    }

    public static void invalidCommand(BasePlugin plugin, CommandSender sender) {
        JsonMessage jsonMessage = new JsonMessage(plugin, "json.command.invalid");
        System.out.println("Json Message: " + jsonMessage.getTranslation());
        jsonMessage.send(sender);
    }

    @SuppressWarnings("unused")
    public static void mustBePlayer(BasePlugin plugin, CommandSender commandSender) {
        new JsonMessage(plugin, "json.must_be_player").send(commandSender);
    }

    public static void noSuchPlayer(BasePlugin plugin, CommandSender commandSender, String player) {
        new JsonMessage(plugin, "json.no_such_player", "player", player).send(commandSender);
    }

    public static void reloadComplete(BasePlugin plugin, CommandSender sender) {
        new JsonMessage(plugin, "json.reload.complete").send(sender);
    }

    public static void reloadFailed(BasePlugin plugin, CommandSender sender) {
        new JsonMessage(plugin, "json.reload.failed").send(sender);
    }

    public static void version(BasePlugin plugin, CommandSender sender) {
        new JsonMessage(plugin, "json.version", "version", plugin.getDescription().getVersion()).send(sender);
    }


    @SuppressWarnings("unused")
    @Deprecated
    public static void sendToAll(BasePlugin plugin, String message) {
        sendToAll(plugin, new TextComponent(message));
    }

    @Deprecated
    public static void send(BasePlugin plugin, CommandSender sender, String message) {
        send(plugin, sender, new TextComponent(message));
    }

    @Deprecated
    public static void sendToAll(BasePlugin plugin, TextComponent component) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            send(plugin, player, component);
        }
    }

    @Deprecated
    public static void send(BasePlugin plugin, CommandSender sender, TextComponent component) {
        // check for NullPointerException
        if (component == null)
            return;
        send(plugin, sender, component, component.getHoverEvent(), component.getClickEvent());
    }

    @Deprecated
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

    private static int longestLength(ArrayList<String> list) {
        String longest = list.get(0);
        for (String str : list) {
            if (str.length() > longest.length()) {
                longest = str;
            }
        }
        return longest.length();
    }


    /**
     * Get a TextComponent with the help for a SubCommand
     *
     * @param plugin  a reference to the plugin
     * @param sender  the command sender
     * @param command a class that is abstracted from AbstractCommand
     * @return the TextComponent with the help.
     */
    public static JsonMessage getHelpForSubCommand(BasePlugin plugin, CommandSender sender, AbstractCommand command) {

        // check for permission
        if (!command.hasPermission(sender)) {
            return null;
        }

        String commandHeadline = "/" + command.getCommandManager().getPluginCommand().getLabel();

        BedrockJson helpJson = BedrockJson.JsonWithText("");
        BedrockJson questionMark = BedrockJson.JsonWithText("<?>").color(JsonColor.DARK_GRAY);
        BedrockJson questionSpace = BedrockJson.Space();
        BedrockJson commandHead = BedrockJson.JsonWithText(commandHeadline).color(JsonColor.PRIMARY);

        helpJson.addExtra(questionMark);
        helpJson.addExtra(questionSpace);
        helpJson.addExtra(commandHead);
        helpJson.addExtra(BedrockJson.Space());

        if (command.getSubcommands() != null) {
            for (String[] commands : command.getSubcommands()) {
                // sort commands by length
                Arrays.sort(commands, new LengthComparator());

                String subCmd = commands[commands.length - 1];
                BedrockJson subCommand = BedrockJson.JsonWithText(subCmd).color(JsonColor.SECONDARY);

                // Add hover with alias
                if (commands.length > 1) {
                    BedrockJson subCommandHover = BedrockJson.JsonWithText(subCmd)
                            .color(JsonColor.SECONDARY)
                            .bold(true);
                    subCommandHover.addExtra(BedrockJson.NewLine());
                    String aliasDesc = new Translation(plugin, "help.subcommand.alias.desc").getTranslation();

                    subCommandHover.addExtra(BedrockJson.JsonWithText(aliasDesc).color(JsonColor.GRAY));

                    for (int i = 0; i < commands.length - 1; i++) {
                        subCommandHover.addExtra(BedrockJson.NewLine());
                        String alias = new Translation(plugin, "help.subcommand.alias.value", new String[]{
                                "alias", commands[i]
                        }).getTranslation();

                        subCommandHover.addExtra(BedrockJson.JsonWithText(alias).color(JsonColor.WHITE));
                    }

                    subCommand.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, subCommandHover);
                }

                helpJson.addExtra(subCommand);
                helpJson.addExtra(BedrockJson.Space());
            }
        }

        /*
         * Description for this command (label)
         *
         * The translation for this description string is taken from the plugin
         * or the BedrockPlugins' locale files
         */
        String commandDescription = "";

        if (command.getDescription() != null && !command.getDescription().isEmpty()) {
            commandDescription = new Translation(plugin, command.getDescription()).getTranslation();
        }

        String[] split = commandDescription.split("\n");

        BedrockJson cmdDesc = BedrockJson.JsonWithText("");

        if (split.length > 0) {
            for (String aSplit : split) {
                cmdDesc.addExtra(BedrockJson.NewLine());
                cmdDesc.addExtra(BedrockJson.JsonWithText(aSplit));
            }
        }

        cmdDesc.addExtra(BedrockJson.Space());

        Boolean hasOptional = false;
        Boolean hasRequired = false;

        // process all arguments
        for (Argument argument : command.getArguments()) {

            if (!argument.userHasPermission(sender)) {
                continue;
            }

            cmdDesc.addExtra(BedrockJson.NewLine());

            /*
             * KeyValueArgument
             *
             * In case the argument is an instanceof the KeyValueArgument class (which is kind of a
             * key-value command) we need to prepend the key
             */
            if (argument instanceof KeyValueArgument) {
                KeyValueArgument keyValueArgument = (KeyValueArgument) argument;

                helpJson.addExtra(BedrockJson.JsonWithText(keyValueArgument.getRuntimeKey()).color(JsonColor.SECONDARY));
                helpJson.addExtra(BedrockJson.Space());

                cmdDesc.addExtra(BedrockJson.JsonWithText(keyValueArgument.getRuntimeKey()).color(JsonColor.GRAY));
                cmdDesc.addExtra(BedrockJson.Space());
            }

            /*
             * Argument placeholder
             */

            Boolean optional = argument.isOptional();


            BedrockJson runtimePlaceholder = BedrockJson.JsonWithText(argument.getRuntimePlaceholder())
                    .color(JsonColor.GRAY)
                    .italic(optional);

            helpJson.addExtra(runtimePlaceholder);
            helpJson.addExtra(BedrockJson.Space());

            if (optional) {
                hasOptional = true;
                String translation = new Translation(plugin, "help.info.arg.optional.symbol").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(translation).color(JsonColor.FLAG));
            } else {
                hasRequired = true;
                String translation = new Translation(plugin, "help.info.arg.required.symbol").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(translation).color(JsonColor.FLAG));
            }
            cmdDesc.addExtra(BedrockJson.Space());
            cmdDesc.addExtra(runtimePlaceholder);

            cmdDesc.addExtra(BedrockJson.JsonWithText("  :  ").color(JsonColor.FLAG));

            cmdDesc.addExtra(BedrockJson.JsonWithText(argument.getRuntimeDescription())).color(JsonColor.WHITE);

        }

        if (command.getArguments() != null && !command.getArguments().isEmpty()) {
            cmdDesc.addExtra(BedrockJson.NewLine());

            // Apply info for command type
            if (hasOptional) {
                cmdDesc.addExtra(BedrockJson.NewLine());

                // Add symbol
                String symbol = new Translation(plugin, "help.info.arg.optional.symbol").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(symbol).color(JsonColor.FLAG));

                // Add blank
                cmdDesc.addExtra(BedrockJson.Space());

                // Add explanation
                String translation = new Translation(plugin, "help.info.arg.optional.desc").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(translation).color(JsonColor.GRAY));
            }

            if (hasRequired) {
                cmdDesc.addExtra(BedrockJson.NewLine());

                // Add symbol
                String symbol = new Translation(plugin, "help.info.arg.required.symbol").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(symbol).color(JsonColor.FLAG));

                // Add blank
                cmdDesc.addExtra(BedrockJson.Space());

                // Add explanation
                String translation = new Translation(plugin, "help.info.arg.required.desc").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(translation).color(JsonColor.GRAY));
            }
        }

        ArrayList<BedrockJson> extras = cmdDesc.getExtras();
        ArrayList<BedrockJson> coloredSuggestion = command.getColoredSuggestion(true);
        for (int i = coloredSuggestion.size() - 1; i >= 0; i--) {
            BedrockJson json = coloredSuggestion.get(i);
            extras.add(0, json);
        }
        cmdDesc.extra(extras);


        questionMark.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, cmdDesc);
        questionSpace.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, cmdDesc);
        commandHead.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, cmdDesc);

        helpJson.clickAction(BedrockClickEvent.Action.SUGGEST_COMMAND, command.getStringSuggestion());

        return new JsonMessage(plugin, helpJson);
    }

    public static String direction(BasePlugin plugin, FacingDirection facingDirection) {
        String string = "";
        switch (facingDirection) {
            case SOUTH:
                string = new Translation(plugin, "direction.south").getTranslation();
                break;
            case SOUTHWEST:
                string = new Translation(plugin, "direction.southwest").getTranslation();
                break;
            case WEST:
                string = new Translation(plugin, "direction.west").getTranslation();
                break;
            case NORTHWEST:
                string = new Translation(plugin, "direction.nothwest").getTranslation();
                break;
            case NORTH:
                string = new Translation(plugin, "direction.north").getTranslation();
                break;
            case NORTHEAST:
                string = new Translation(plugin, "direction.northeast").getTranslation();
                break;
            case EAST:
                string = new Translation(plugin, "direction.east").getTranslation();
                break;
            case SOUTHEAST:
                string = new Translation(plugin, "direction.southeast").getTranslation();
                break;
        }
        return string;
    }
}
