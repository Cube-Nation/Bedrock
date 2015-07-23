package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
    public void execute(CommandSender sender, String label, String[] subCommands, String[] args) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Display help for all commands

            ChatColor primary = commandManager.getPlugin().getPrimaryColor();
            ChatColor secondary = commandManager.getPlugin().getSecondaryColor();
            ChatColor flag = commandManager.getPlugin().getFlagColor();



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
                TextComponent subCommandHelp = commandManager.getHelpForSubCommand(subCommand, sender, label);
                if (subCommandHelp != null) {
                    commandsList.add(subCommandHelp);
                }
            }

            for (TextComponent textComponent : commandsList) {
                player.spigot().sendMessage(textComponent);
            }

        } else if (isNumeric(args[0])) {
            player.sendMessage("Zeige (irgendwann) Seite: " +  args[0] + " der Hilfe.");
        } else {
            for (SubCommand subCommand : commandManager.getSubCommands()) {
                if (Arrays.asList(subCommand.getCommands().get(0)).contains(args[0])) {
                    player.spigot().sendMessage(commandManager.getHelpForSubCommand(subCommand, player, label));
                }
            }
        }




    }

    private boolean isNumeric(String arg) {
        try {
            int i = Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
