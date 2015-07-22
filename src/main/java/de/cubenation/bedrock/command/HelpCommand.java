package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.helper.LengthComparator;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;


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
    public void execute(CommandSender sender, String label,String[] subCommands, String[] args) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        ChatColor primary   = commandManager.getPlugin().getPrimaryColor();
        ChatColor secondary = commandManager.getPlugin().getSecondaryColor();
        ChatColor flag      = commandManager.getPlugin().getFlagColor();
        Player player       = (Player) sender;


        // =========================
        // create header
        // =========================
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);
        if (helpPrefix != null) {
            commandHeaderName = helpPrefix;
        }

        ComponentBuilder header = new ComponentBuilder("==== ").color(primary)
                .append(commandHeaderName + " Help").color(primary)
                .append(" ====").color(flag);
        player.spigot().sendMessage(header.create());


        // =========================
        // create help for subcommand
        // =========================
        for (SubCommand subCommand : commandManager.getSubCommands()) {

            // check permission
            if (!subCommand.hasPermission(sender)) {
                continue;
            }

            // the command help component
            TextComponent command_help = new TextComponent("/" + label);
            command_help.setColor(primary);

            // create duplicate suggest from command_help
            TextComponent _suggest_command = (TextComponent) command_help.duplicate();

            // add subcommands
            if (subCommand.getCommands() != null) {
                for (String[] commands : subCommand.getCommands()) {
                    Arrays.sort(commands, new LengthComparator());

                    for (String c : commands) {
                        TextComponent subcommand_help = new TextComponent(c);
                        subcommand_help.setColor(secondary);
                        subcommand_help.addExtra("|");
                    }

                    _suggest_command.addExtra(" " + commands[0]);
                }
            }


            // =========================
            // create tooltip
            // =========================
            TextComponent tooltip = new TextComponent();

            for (String helpString : subCommand.getHelp()) {
                TextComponent _tooltip = new TextComponent("\n" + ChatColor.WHITE + helpString);
                tooltip.addExtra(_tooltip);
            }
/*
            if (subCommand.getArguments() != null) {
                for (Map.Entry<String, String> entry : subCommand.getArguments().entrySet()) {
                    cmdWithArgument += " " + entry.getKey();
                    TextComponent _tooltip_sub = new TextComponent("\n" + entry.getKey());

                    if (entry.getValue() != null) {
                        _tooltip_sub.addExtra(" - " + entry.getValue());
                    }

                    _tooltip_sub.setColor(ChatColor.GRAY);
                    _tooltip_sub.setItalic(true);
                    tooltip.addExtra(_tooltip_sub);
                }

            }
*/
            //String help = secondary + "" + cmdWithArgument + toolTipHelp;

            // assign click event (suggest command)
            command_help.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, _suggest_command.toString()));

            // assign hover event (tooltip)
            //command_help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, _hover_text));

            // send message
            player.spigot().sendMessage(command_help);
        } // for
    }


    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }

    public void setHelpPrefix(String helpPrefix) {
        this.helpPrefix = helpPrefix;
    }
}
