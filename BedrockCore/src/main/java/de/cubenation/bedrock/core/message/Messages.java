/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *  
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.message;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.AbstractCommand;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.command.argument.Argument;
import de.cubenation.bedrock.core.command.argument.KeyValueArgument;
import de.cubenation.bedrock.core.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.core.helper.LengthComparator;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.core.authorization.Permission;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.Translation;
import de.cubenation.bedrock.core.translation.parts.BedrockClickEvent;
import de.cubenation.bedrock.core.translation.parts.BedrockHoverEvent;
import de.cubenation.bedrock.core.translation.parts.BedrockJson;
import de.cubenation.bedrock.core.translation.parts.JsonColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class Messages {

    private final FoundationPlugin plugin;

    private final Error error;

    public Messages(FoundationPlugin foundationPlugin) {
        this.plugin = foundationPlugin;
        error = new Error();
    }



    public FoundationPlugin getPlugin() {
        return plugin;
    }

    public Error error() {
        return error;
    }

    public void commandExecutionError(BedrockChatSender sender, Exception e) {
        sender.sendMessage(getPlugin().getMessagePrefix() + " " + e.getMessage());
    }

    public void insufficientPermission(BedrockChatSender sender) {
        new JsonMessage(getPlugin(), "permission.insufficient").send(sender);
    }

    public void noPermission(BedrockChatSender sender) {
        new JsonMessage(getPlugin(), "json.no_permissions").send(sender);
    }

    public void invalidCommand(BedrockChatSender sender) {
        JsonMessage jsonMessage = new JsonMessage(getPlugin(), "command.invalid");
        jsonMessage.send(sender);
    }

    public void mustBePlayer(BedrockChatSender commandSender) {
        new JsonMessage(getPlugin(), "must_be_player").send(commandSender);
    }

    public void noSuchPlayer(BedrockChatSender commandSender, String player) {
        new JsonMessage(getPlugin(), "no_such_player.specific", "player", player).send(commandSender);
    }

    public void noSuchPlayer(BedrockChatSender commandSender) {
        new JsonMessage(plugin, "no_such_player.default").send(commandSender);
    }

    public void noSuchWorld(BedrockChatSender commandSender, String world) {
        if (world == null || world.equals("")) {
            noSuchWorld(commandSender);
            return;
        }
        new JsonMessage(plugin, "no_such_world", "world", world).send(commandSender);
    }

    public void noSuchWorld(BedrockChatSender commandSender) {
        new JsonMessage(plugin, "no_such_world_empty").send(commandSender);
    }

    public void reloadComplete(BedrockChatSender sender) {
        new JsonMessage(plugin, "reload.complete").send(sender);
    }

    public void reloadFailed(BedrockChatSender sender) {
        new JsonMessage(plugin, "reload.failed").send(sender);
    }

    public void version(BedrockChatSender sender) {
        new JsonMessage(plugin, "version", "version", plugin.getPluginDescription().getVersion()).send(sender);
    }

    @Deprecated
    public void send(BedrockChatSender sender, String message) {
        send(sender, new TextComponent(message));
    }

    @Deprecated
    public void send(BedrockChatSender sender, TextComponent component) {
        // check for NullPointerException
        if (component == null)
            return;
        send(sender, component, component.getHoverEvent(), component.getClickEvent());
    }

    @Deprecated
    public void send(BedrockChatSender sender, TextComponent component, HoverEvent hover_event, ClickEvent click_event) {
        // check for NullPointerException
        if (component == null)
            return;

        // color scheme service
        ColorScheme color_scheme = plugin.getColorSchemeService().getColorScheme();

        // apply colors from color scheme to message
        component = color_scheme.applyColorScheme(component);

        if (sender instanceof BedrockPlayer) {
            if (hover_event != null)
                component.setHoverEvent(color_scheme.applyColorScheme(hover_event));
            if (click_event != null)
                component.setClickEvent(color_scheme.applyColorScheme(click_event));

            ((BedrockPlayer) sender).sendMessage(component);

        } else {
            StringBuilder hover_message = new StringBuilder();
            // ClickEvents are not supported here
            //String click_message = "";

            if (hover_event != null) {
                hover_event = color_scheme.applyColorScheme(hover_event);

                for (int i = 0; i < hover_event.getValue().length; i++) {
                    hover_message.append(hover_event.getValue()[i].toLegacyText());
                }

                // check for multiline string
                if (hover_message.toString().contains("\n")) {

                    ArrayList<String> lines = new ArrayList<>();
                    lines.addAll(Arrays.asList(hover_message.toString().split("\\r?\\n")));

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
                    hover_message = new StringBuilder("\n" + StringUtils.join(lines, System.lineSeparator()));

                } else {
                    hover_message = new StringBuilder(" " +   // make explicit string
                            ChatColor.DARK_GRAY + "[ " + ChatColor.RESET +
                            hover_message +
                            ChatColor.DARK_GRAY + " ]" + ChatColor.RESET);
                }
            }

            // finally send message
            sender.sendMessage(component.toLegacyText() + hover_message);
        }
    }

    private int longestLength(ArrayList<String> list) {
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
     * @param sender  the command sender
     * @param command a class that is abstracted from AbstractCommand
     * @return the TextComponent with the help.
     */
    public JsonMessage getHelpForSubCommand(BedrockChatSender sender, AbstractCommand command) {

        // check for permission
        if (!command.hasPermission(sender)) {
            return null;
        }

        String commandHeadline = "/" + command.getCommandManager().getLabel();

        BedrockJson helpJson = BedrockJson.JsonWithText("");
        BedrockJson questionMark = BedrockJson.JsonWithText("<?>").color(JsonColor.GRAY);
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

        // process all Arguments
        for (Argument argument : command.getArguments()) {

            if (!argument.userHasPermission(sender)) {
                continue;
            }

            cmdDesc.addExtra(BedrockJson.NewLine());

            Boolean optional = argument.isOptional();
            if (optional) {
                hasOptional = true;
                String translation = new Translation(plugin, "help.info.arg.optional.symbol").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(translation).color(JsonColor.FLAG));
            } else {
                hasRequired = true;
                String translation = new Translation(plugin, "help.info.arg.required.symbol").getTranslation();
                cmdDesc.addExtra(BedrockJson.JsonWithText(translation).color(JsonColor.FLAG));
            }

            /*
             * KeyValueArgument
             *
             * In case the argument is an instanceof the KeyValueArgument class (which is kind of a
             * key-value command) we need to prepend the key
             */
            if (argument instanceof KeyValueArgument) {
                KeyValueArgument keyValueArgument = (KeyValueArgument) argument;

                String keyString = "[" + keyValueArgument.getKey() + (keyValueArgument.getKeyOnly() ? "]" : "");

                helpJson.addExtra(BedrockJson.JsonWithText(keyString).color(JsonColor.SECONDARY));
                helpJson.addExtra(BedrockJson.Space());

                cmdDesc.addExtra(BedrockJson.Space());
                cmdDesc.addExtra(BedrockJson.JsonWithText(keyString).color(JsonColor.SECONDARY));
                if (!keyValueArgument.getKeyOnly()) {
                    cmdDesc.addExtra(BedrockJson.Space());
                }
            }

            /*
             * Argument placeholder
             */

            if (!(argument instanceof KeyValueArgument)
                    || !((KeyValueArgument) argument).getKeyOnly()) {
                BedrockJson runtimePlaceholder = null;

                if (argument instanceof KeyValueArgument) {
                    runtimePlaceholder = BedrockJson.JsonWithText((argument.isOptional()
                            ? argument.getRuntimePlaceholder() + "]"
                            : argument.getRuntimePlaceholder()))
                            .color(JsonColor.GRAY)
                            .italic(optional);
                } else {
                    runtimePlaceholder = BedrockJson.JsonWithText((argument.isOptional()
                            ? "[" + argument.getRuntimePlaceholder() + "]"
                            : argument.getRuntimePlaceholder()))
                            .color(JsonColor.GRAY)
                            .italic(optional);
                }


                helpJson.addExtra(runtimePlaceholder);
                helpJson.addExtra(BedrockJson.Space());

                cmdDesc.addExtra(BedrockJson.Space());
                cmdDesc.addExtra(runtimePlaceholder);
            }

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

    public String getPlainText(String localeIdentifier, String... localeArgs) {
        // try to get the localized string from the plugins locale file
        try {
            return plugin.getLocalizationService().getTranslation(localeIdentifier, localeArgs);
        } catch (LocalizationNotFoundException ignored) {
        }

        if (!plugin.isFallbackBedrockPlugin()) {
            // if the above failed, we try to get the string from Bedrocks locale file
            try {
                return plugin.getFallbackBedrockPlugin().getLocalizationService().getTranslation(
                        localeIdentifier, localeArgs
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

    public void displayCommandList(BedrockChatSender sender, HashMap<String, String> commandList) {
        new JsonMessage(plugin, "plugin.command.list.header").send(sender);

        for (Map.Entry<String, String> entry : commandList.entrySet()) {
            new JsonMessage(plugin, "plugin.command.list.entry",
                    "command", entry.getKey(),
                    "description", entry.getValue()
            ).send(sender);
        }
    }

    public void displayPermissions(BedrockChatSender sender, List<Permission> permissions) {
        String permission_prefix = plugin.getPermissionService().getPermissionPrefix();

        new JsonMessage(plugin, "permission.list.header").send(sender);
        permissions.stream()
                .map(Permission::getRole)
                .distinct()
                .collect(Collectors.toList())
                .forEach(commandRole -> {
                    new JsonMessage(plugin, "permission.list.role",
                            "role", String.format("%s.%s", permission_prefix, commandRole.getType().toLowerCase())
                    ).send(sender);

                    permissions.stream()
                            .filter(permission -> permission.getRole().equals(commandRole))
                            .collect(Collectors.toList()).forEach(permission -> {

                        // avoid empty description message
                        String description = new Translation(plugin, permission.getDescriptionLocaleIdent()).getTranslation();
                        if (description.isEmpty()) description = "No permission help available for " + permission.getDescriptionLocaleIdent();

                        new JsonMessage(plugin, "permission.list.permission",
                                "permission", permission.getPermissionNode(),
                                "description", description
                        ).send(sender);
                    });
                });
    }

    public class Bypass {

        public void Info(BedrockPlayer player) {
            new JsonMessage(plugin, "bypass.used").send(player, ChatMessageType.ACTION_BAR);
        }

    }

    public class Error {

        public void SettingsNotFound(BedrockChatSender sender, String key) {
            new JsonMessage(plugin, "no_such_setting", "key", key).send(sender);
        }

    }
}
