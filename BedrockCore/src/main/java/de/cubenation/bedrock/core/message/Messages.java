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
import de.cubenation.bedrock.core.authorization.Permission;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.argument.Argument;
import de.cubenation.bedrock.core.command.argument.Option;
import de.cubenation.bedrock.core.command.tree.CommandTreePath;
import de.cubenation.bedrock.core.command.tree.CommandTreePathItem;
import de.cubenation.bedrock.core.exception.LocalizationNotFoundException;
import de.cubenation.bedrock.core.service.colorscheme.ColorScheme;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.Translation;
import de.cubenation.bedrock.core.translation.parts.BedrockClickEvent;
import de.cubenation.bedrock.core.translation.parts.BedrockHoverEvent;
import de.cubenation.bedrock.core.translation.parts.BedrockJson;
import de.cubenation.bedrock.core.translation.parts.JsonColor;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import lombok.Getter;
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
 * @version 2.0
 */
public abstract class Messages {

    @Getter
    private final FoundationPlugin plugin;

    private final Error error;

    public Messages(FoundationPlugin foundationPlugin) {
        this.plugin = foundationPlugin;
        error = new Error();
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

    public JsonMessage getNoSuchPlayer(String player) {
        return new JsonMessage(getPlugin(), "no_such_player.specific", "player", player);
    }

    public void noSuchPlayer(BedrockChatSender commandSender, String player) {
        getNoSuchPlayer(player).send(commandSender);
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

    public JsonMessage getNoValidInt(String input) {
        return new JsonMessage(plugin, "no_valid_int", "input", input);
    }

    public JsonMessage getNoValidFloat(String input) {
        return new JsonMessage(plugin, "no_valid_float", "input", input);
    }

    public JsonMessage getNoValidUuid(String input) {
        return new JsonMessage(plugin, "no_valid_uuid", "input", input);
    }

    public JsonMessage getNoValidEnumConstant(String input, List<String> constantList) {
        String constants = String.join(", ", constantList);
        return new JsonMessage(plugin, "no_valid_enum_constant", "input", input, "constants", constants);
    }

    public JsonMessage getGreaterThan(String input, int max) {
        return new JsonMessage(plugin, "greater_than", "input", input, "max", Integer.toString(max));
    }

    public JsonMessage getLowerThan(String input, int min) {
        return new JsonMessage(plugin, "lower_than", "input", input, "min", Integer.toString(min));
    }

    public JsonMessage getStringTooLong(String input, int max) {
        return new JsonMessage(plugin, "string_too_long", "input", input, "max", Integer.toString(max));
    }

    public JsonMessage getStringTooShort(String input, int min) {
        return new JsonMessage(plugin, "string_too_short", "input", input, "min", Integer.toString(min));
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
     * @return the TextComponent with the help.
     */
    public JsonMessage getHelpForSubCommand(BedrockChatSender sender, CommandTreePath treePath, Command command) {

        CommandTreePathItem[] callStack = treePath.getAll();;

        String commandHeadline = "/" + callStack[0].getCalledLabel();

        BedrockJson coloredCommandJson = BedrockJson.JsonWithText("");

        BedrockJson helpJson = BedrockJson.JsonWithText("");
        BedrockJson questionMark = BedrockJson.JsonWithText("<?>").color(JsonColor.GRAY);
        BedrockJson questionSpace = BedrockJson.Space();
        BedrockJson commandHead = BedrockJson.JsonWithText(commandHeadline).color(JsonColor.PRIMARY);

        coloredCommandJson.addExtra(commandHead.clone());
        coloredCommandJson.addExtra(BedrockJson.Space());

        helpJson.addExtra(questionMark);
        helpJson.addExtra(questionSpace);
        helpJson.addExtra(commandHead);
        helpJson.addExtra(BedrockJson.Space());

        CommandTreePathItem[] subCommands = Arrays.copyOfRange(callStack, 1, callStack.length);
        for (CommandTreePathItem subCmd : subCommands) {
            BedrockJson subCommand = BedrockJson.JsonWithText(subCmd.getCalledLabel()).color(JsonColor.SECONDARY);

            coloredCommandJson.addExtra(subCommand.clone());
            coloredCommandJson.addExtra(BedrockJson.Space());

            // Add hover
            BedrockJson subCommandHover = BedrockJson.JsonWithText(subCmd.getCalledLabel())
                    .color(JsonColor.SECONDARY)
                    .bold(true);
            if (subCmd.getAliases().length > 0) {
                // Aliases
                subCommandHover.addExtra(BedrockJson.NewLine());
                String aliasDesc = new Translation(plugin, "help.subcommand.alias.desc").getTranslation();

                subCommandHover.addExtra(BedrockJson.JsonWithText(aliasDesc).color(JsonColor.GRAY));

                for (int i = 0; i < subCmd.getAliases().length; i++) {
                    subCommandHover.addExtra(BedrockJson.NewLine());
                    String alias = new Translation(plugin, "help.subcommand.alias.value", new String[]{
                            "alias", subCmd.getAliases()[i]
                    }).getTranslation();

                    subCommandHover.addExtra(BedrockJson.JsonWithText(alias).color(JsonColor.WHITE));
                }
            }
            subCommand.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, subCommandHover);

            helpJson.addExtra(subCommand);
            helpJson.addExtra(BedrockJson.Space());
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

        boolean separatorLinePlaced = false;

        // process all Arguments
        for (Argument argument : command.getArguments()) {

            if (!argument.userHasPermission(sender)) {
                continue;
            }

            cmdDesc.addExtra(BedrockJson.NewLine());

            // Create argument placeholder
            boolean optional = argument.isOptional();
            StringBuilder argumentContent = new StringBuilder();
            if (argument instanceof Option option) {
                argumentContent.append("-");
                argumentContent.append(option.getKey());
                if (option.hasParameter()) {
                    argumentContent.append(" ");
                    argumentContent.append(argument.getRuntimePlaceholder());
                }
            } else {
                argumentContent.append(argument.getRuntimePlaceholder());
            }

            BedrockJson argumentJson = BedrockJson.JsonWithText((optional
                            ? "[" + argumentContent + "]"
                            : argumentContent.toString())
                    )
                    .color(JsonColor.GRAY);

            // Add argument Hover
            BedrockJson argumentHoverJson = argumentJson.clone();
            argumentHoverJson.addExtra(BedrockJson.NewLine());
            argumentHoverJson.addExtra(BedrockJson.JsonWithText(argument.getRuntimeDescription()).color(JsonColor.WHITE));
            argumentJson.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, argumentHoverJson);

            // Add inline argument
            if (!(argument instanceof Option option && option.isHidden())) {
                helpJson.addExtra(argumentJson);
                helpJson.addExtra(BedrockJson.Space());
            }

            // Add arg to hover help
            if (!separatorLinePlaced) {
                cmdDesc.addExtra(BedrockJson.NewLine());
                separatorLinePlaced = true;
            }
            cmdDesc.addExtra(argumentJson);
            cmdDesc.addExtra(BedrockJson.JsonWithText(" : ").color(JsonColor.FLAG));
            cmdDesc.addExtra(BedrockJson.JsonWithText(argument.getRuntimeDescription())).color(JsonColor.WHITE);

        }

        // Add command in front of command description
        ArrayList<BedrockJson> extras = cmdDesc.getExtras();
        extras.add(0, coloredCommandJson);
        cmdDesc.extra(extras);

        // Add hover to base elements
        questionMark.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, cmdDesc);
        questionSpace.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, cmdDesc);
        commandHead.hoverAction(BedrockHoverEvent.Action.SHOW_TEXT, cmdDesc);

        // Add click action
        helpJson.clickAction(BedrockClickEvent.Action.SUGGEST_COMMAND, "/"+treePath.getCommandAsString());

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
