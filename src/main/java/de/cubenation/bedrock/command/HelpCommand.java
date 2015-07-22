package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.helper.LengthComparator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends SubCommand {

    private final CommandManager commandManager;
    private String helpPrefix;

    public HelpCommand(CommandManager commandManager, String helpPrefix) {
        super("help", new String[]{"Plugin help"}, null);
        this.commandManager = commandManager;
        this.helpPrefix = helpPrefix;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subCommands, String[] args) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        ChatColor primary = commandManager.getPlugin().getPrimaryColor();
        ChatColor secondary = commandManager.getPlugin().getSecondaryColor();
        ChatColor flag = commandManager.getPlugin().getFlagColor();
        Player player = (Player) sender;


        // =========================
        // create header
        // =========================
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);
        if (helpPrefix != null) {
            commandHeaderName = helpPrefix;
        }

        ComponentBuilder header = new ComponentBuilder("==== ").color(flag)
                .append(commandHeaderName + " Help").color(primary)
                .append(" ====").color(flag);

        // =========================
        // create help for each subcommand
        // =========================

        ArrayList<TextComponent> commandsList = new ArrayList<>();
        commandsList.add(new TextComponent(header.create()));

        for (SubCommand subCommand : commandManager.getSubCommands()) {
            if (subCommand.hasPermission(sender)) {

                String command = primary + "/" + label + "" +
                        secondary;
                String useCommand = command;
                if (subCommand.getCommands() != null) {
                    for (String[] commands : subCommand.getCommands()) {
                        Arrays.sort(commands, new LengthComparator());
                        command += " " + StringUtils.join(commands, primary + "|" + secondary);
                        useCommand += " " + commands[0];
                    }
                }

                String cmdWithArgument = command;

                String toolTipHelp = "";
                for (String helpString : subCommand.getHelp()) {
                    toolTipHelp += "\n" + ChatColor.WHITE + helpString;
                }

                if (subCommand.getArguments() != null) {
                    for (Map.Entry<String, String> entry : subCommand.getArguments().entrySet()) {
                        cmdWithArgument += " " + entry.getKey();
                        toolTipHelp += "\n" + ChatColor.GRAY + ChatColor.ITALIC + entry.getKey();
                        if (entry.getValue() != null) {
                            toolTipHelp += " - " + entry.getValue();
                        }
                    }
                }

                String help = secondary + "" + cmdWithArgument + toolTipHelp;

                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(cmdWithArgument));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(useCommand)));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(help)));

                commandsList.add(textComponent);
            }
        }

        for (TextComponent textComponent : commandsList) {
            player.spigot().sendMessage(textComponent);
        }

    }


    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }

    public void setHelpPrefix(String helpPrefix) {
        this.helpPrefix = helpPrefix;
    }
}
