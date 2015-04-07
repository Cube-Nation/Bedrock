package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.message.MessageComponentAction;
import de.cubenation.bedrock.message.MessagePart;
import de.cubenation.bedrock.message.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends SubCommand {


    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", new String[]{"Hilfe"}, null);
        this.commandManager = commandManager;
    }


    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {

        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;

            PlayerMessage playerMessage = new PlayerMessage();
            playerMessage.addComponent(new MessagePart("==== " + commandManager.getPlugin().getName() + " Help ====").color(ChatColor.GOLD))
                    .send(player);
        }

        for (SubCommand subCommand : commandManager.getSubCommands()) {
            if (subCommand.hasPermission(sender)) {

                String usage = "/" + label + " " + subCommand.getName() + (subCommand.getArgumentsHelp() != null ? " " + subCommand.getArgumentsHelp() : "");

                String help = ChatColor.GOLD + "" + ChatColor.BOLD + usage + ChatColor.RESET;
                for (String helpString : subCommand.getHelp()) {
                    help += "\n" + ChatColor.WHITE + helpString;
                }

                if (player != null) {
                    PlayerMessage playerMessage = new PlayerMessage();
                    playerMessage.addComponent(new MessagePart(usage).color(ChatColor.GOLD)
                                    .action(MessageComponentAction.SUGGEST_COMMAND, usage)
                                    .tooltip(help)
                    ).send(player);
                }
            }
        }
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public String getArgumentsHelp() {
        return null;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public String[] getTabCompletionListForArgument(int argument) {
        return null;
    }

}
