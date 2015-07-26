package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.permission.PermissionListCommand;
import de.cubenation.bedrock.command.permission.PermissionReloadCommand;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.LengthComparator;
import de.cubenation.bedrock.helper.MessageHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private BasePlugin plugin;

    private PluginCommand pluginCommand;

    private List<SubCommand> subCommands = new ArrayList<>();

    private String helpPrefix;

    private HelpCommand helpCommand = new HelpCommand(this, helpPrefix);


    public CommandManager(BasePlugin plugin, PluginCommand pluginCommand, String helpPrefix, SubCommand... subCommands) {
        init(plugin, pluginCommand, helpPrefix, subCommands);
    }

    private void init(BasePlugin plugin, PluginCommand pluginCommand, String helpPrefix, SubCommand[] subCommands) {
        this.plugin         = plugin;
        this.pluginCommand  = pluginCommand;
        this.helpPrefix     = helpPrefix;

        if (subCommands != null)
            Collections.addAll(this.subCommands, subCommands);

        this.subCommands.add(helpCommand);
        helpCommand.setHelpPrefix(helpPrefix);

        if (plugin.usePermissionService()) {
            this.subCommands.add(new PermissionReloadCommand());
        }

        // add default commands that all plugins are capable of
        this.subCommands.add(new PermissionListCommand());
        this.subCommands.add(new ReloadCommand());
        this.subCommands.add(new VersionCommand());

        this.setCommandManager();
    }

    private void setCommandManager() {
        for (SubCommand subCommand : subCommands) {
            subCommand.setCommandManager(this);
        }
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            try {
                helpCommand.execute(commandSender, label, null, args);

            } catch (CommandException e) {
                MessageHelper.commandExecutionError(this.plugin, commandSender, e);
                e.printStackTrace();

            }
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.isValidTrigger(args)) {

                if (!subCommand.hasPermission(commandSender)) {
                    MessageHelper.insufficientPermission(plugin, commandSender);
                    return true;
                }

                try {
                    subCommand.execute(
                            commandSender,
                            label,
                            Arrays.copyOfRange(args, 0, subCommand.getCommands().size()),
                            Arrays.copyOfRange(args, subCommand.getCommands().size(), args.length));
                    return true;

                } catch (CommandException e) {
                    MessageHelper.commandExecutionError(this.plugin, commandSender, e);
                    e.printStackTrace();

                } catch (IllegalCommandArgumentException e) {
                    MessageHelper.invalidCommand(this.plugin, commandSender);
                    MessageHelper.send(this.plugin, commandSender, getHelpForSubCommand(subCommand, commandSender, label));

                }
                return true;
            }
        }

        // was genau wird hier gemacht? optimierungsbedarf?
        boolean canHelp = false;
        for (SubCommand subCommand : subCommands) {
            if (subCommand.isValidHelpTrigger(args)) {

                // TODO: warum nur wenn player?
                if (commandSender instanceof Player) {
                    /*
                    Player player = (Player) commandSender;
                    player.spigot().sendMessage(getHelpForSubCommand(subCommand, player, label));
                    */
                    MessageHelper.send(this.plugin, commandSender, getHelpForSubCommand(subCommand, commandSender, label));
                }
                canHelp = true;
            }
        }
        if (canHelp) {
            return true;
        }

        // unknown command
        MessageHelper.invalidCommand(this.plugin, commandSender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completionList = new ArrayList<>();

        for (SubCommand subCommand : subCommands) {

            if (!subCommand.hasPermission(sender)) {
                continue;
            }

            String completionCommand = subCommand.getTabCompletionListForArgument(args);
            if (completionCommand != null) {
//                for (String completionCommand : completionCommands) {
                    if (!completionList.contains(completionCommand)) {
                        if (!args[args.length - 1].equals("")) {
                            if (completionCommand.startsWith(args[args.length - 1])) {
                                completionList.add(completionCommand);
                            }
                        } else {
                            completionList.add(completionCommand);
                        }
//                    }
                }
            }

        }

        return (completionList.isEmpty()) ? null : completionList;
    }

    /**
     * Get a TextComponent with the help for a SubCommand
     *
     * @param subCommand the SubCommand
     * @param sender     the command sender
     * @param label      the label of the command
     * @return the TextComponent with the help.
     */
    public TextComponent getHelpForSubCommand(SubCommand subCommand, CommandSender sender, String label) {

        if (!subCommand.hasPermission(sender)) {
            return null;
        }

        ChatColor primary   = this.getPlugin().getColorScheme().getPrimary();
        ChatColor secondary = this.getPlugin().getColorScheme().getSecondary();

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
            toolTipHelp += System.lineSeparator() + ChatColor.WHITE + helpString;
        }

        if (subCommand.getArguments() != null) {
            for (Map.Entry<String, String> entry : subCommand.getArguments().entrySet()) {
                cmdWithArgument += " " + entry.getKey();
                toolTipHelp +=  System.lineSeparator() +
                                ChatColor.GRAY + ChatColor.ITALIC + entry.getKey() +
                                ChatColor.RESET;

                if (entry.getValue() != null)
                    toolTipHelp += " - " + entry.getValue();
                    // FIXME: somehow the reset does not work -> looks ugly in Console
                    //toolTipHelp += ChatColor.ITALIC + " - " + entry.getValue() + ChatColor.RESET;

            }
        }

        String help = secondary + "" + cmdWithArgument + toolTipHelp;

        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(cmdWithArgument));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(useCommand)));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(help)));

        return textComponent;
    }


    //region Getter
    public BasePlugin getPlugin() {
        return plugin;
    }

    public PluginCommand getPluginCommand() {
        return pluginCommand;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
    //endregion
}