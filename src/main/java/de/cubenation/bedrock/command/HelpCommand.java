package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.message.JsonMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends SubCommand {

    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", new String[]{"Plugin help"}, null);
        this.commandManager = commandManager;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {

        Player player = null;

        ChatColor primary = commandManager.getPlugin().getPrimaryColor();
        ChatColor secondary = commandManager.getPlugin().getSecondaryColor();

        if (sender instanceof Player) {
            player = (Player) sender;

            String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);

            String header = ChatColor.WHITE + "==== " +
                    primary +
                    commandHeaderName
                    + " Help " +
                    ChatColor.WHITE + "==== ";

            new JsonMessage(header).send(player);
        }

        for (SubCommand subCommand : commandManager.getSubCommands()) {
            if (subCommand.hasPermission(sender)) {

                String command = primary + "/" + label + "" +
                        secondary;
                String useCommand = command;
                if (subCommand.getCommands() != null) {
                    for (String[] commands : subCommand.getCommands()) {
                        command += " " + StringUtils.join(commands, '|');
                        useCommand += " " + commands[0];
                    }
                }

                String cmdWithArgument = command;

                String toolTipHelp = "";
                for (String helpString : subCommand.getHelp()) {
                    toolTipHelp += "\n" + ChatColor.GRAY + helpString;
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

                if (player != null) {

                    new JsonMessage(cmdWithArgument)
                            .suggest(ChatColor.stripColor(useCommand))
                            .tooltip(help)
                            .send(player);
                }
            }
        }
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }


}
