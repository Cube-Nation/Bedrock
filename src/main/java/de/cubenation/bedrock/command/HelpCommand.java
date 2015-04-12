package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.message.MessageComponentAction;
import de.cubenation.bedrock.message.MessagePart;
import de.cubenation.bedrock.message.PlayerMessage;
import de.cubenation.bedrock.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends SubCommand {


    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", new String[]{"Hilfe für Plugin xy"}, null);
        this.commandManager = commandManager;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {

        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;

            String commandHeaderName = "";
            if (commandManager.getHelpPrefix() != null) {
                commandHeaderName = commandManager.getHelpPrefix();
            } else {
                commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);;
            }
            PlayerMessage playerMessage = new PlayerMessage();


            String header = ChatColor.WHITE + "==== " +
                    BasePlugin.getInstance().getPrimaryColor() +
                    commandHeaderName
                    + " Help " +
                    ChatColor.WHITE + "==== ";

            playerMessage.addComponent(new MessagePart(header))
                    .send(player);
        }

        for (SubCommand subCommand : commandManager.getSubCommands()) {
            if (subCommand.hasPermission(sender)) {

                String command = BasePlugin.getInstance().getPrimaryColor() + "/" + label + " " +
                        BasePlugin.getInstance().getSecondaryColor();
                String useCommand = command;
                if (subCommand.getCommands() != null) {
                    for (String[] commands : subCommand.getCommands()) {
                        command += StringUtils.join(commands, '|') + " ";
                        useCommand += commands[0] + " ";
                    }
                }

                String cmdWithArgument = command;

                String toolTipHelp ="";
                for (String helpString : subCommand.getHelp()) {
                    toolTipHelp += "\n" + ChatColor.GRAY + helpString;
                }

                if (subCommand.getArguments() != null) {
                    for (Map.Entry<String, String> entry : subCommand.getArguments().entrySet()) {
                        cmdWithArgument += entry.getKey() + " ";
                        toolTipHelp += "\n" + ChatColor.GRAY + ChatColor.ITALIC + entry.getKey();
                        if (entry.getValue() != null) {
                            toolTipHelp += " - " + entry.getValue();
                        }
                    }
                }

                String help = BasePlugin.getInstance().getSecondaryColor() + "" + cmdWithArgument + toolTipHelp;

                if (player != null) {
                    PlayerMessage playerMessage = new PlayerMessage();
                    playerMessage.addComponent(new MessagePart(cmdWithArgument)
                                    .action(MessageComponentAction.SUGGEST_COMMAND, ChatColor.stripColor(useCommand))
                                    .tooltip(help)
                    ).send(player);
                }
            }
        }
    }

    @Override
    public HashMap<String, String> getArguments() {
        return null;
    }


}
