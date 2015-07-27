package de.cubenation.bedrock.command.manager;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.command.predefined.*;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.MessageHelper;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private BasePlugin plugin;

    private PluginCommand pluginCommand;

    private List<SubCommand> subCommands = new ArrayList<>();

    private List<AbstractCommand> commands = new ArrayList<>();

    private String helpPrefix;

    private HelpCommand helpCommand = new HelpCommand(this, helpPrefix);


//    public CommandManager(BasePlugin plugin, PluginCommand pluginCommand, String helpPrefix, SubCommand... subCommands) {
//        this.plugin = plugin;
//        this.pluginCommand = pluginCommand;
//        this.helpPrefix = helpPrefix;
//
//        Collections.addAll(this.subCommands, subCommands);
//
//        this.subCommands.add(helpCommand);
//        helpCommand.setHelpPrefix(helpPrefix);
//
//        if (plugin.usePermissionService()) {
//            this.subCommands.add(new PermissionReloadCommand());
//        }
//
//        // add default commands that all plugins are capable of
//        this.subCommands.add(new PermissionListCommand());
//        this.subCommands.add(new ReloadCommand());
//        this.subCommands.add(new VersionCommand());
//
//        this.setCommandManager();
//    }

    public CommandManager(BasePlugin plugin, PluginCommand pluginCommand, String helpPrefix, Command... commands) {
        this.plugin = plugin;
        this.pluginCommand = pluginCommand;
        this.helpPrefix = helpPrefix;

        Collections.addAll(this.commands, commands);

        this.commands.add(helpCommand);
        helpCommand.setHelpPrefix(helpPrefix);

        //FIXME Later
//        if (plugin.usePermissionService()) {
//            this.subCommands.add(new PermissionReloadCommand());
//        }
//
//        // add default commands that all plugins are capable of
//        this.subCommands.add(new PermissionListCommand());
//        this.subCommands.add(new ReloadCommand());
//        this.subCommands.add(new VersionCommand());

        for (AbstractCommand command : this.commands) {
            command.addCommandManager(this);
        }
    }

    private void setCommandManager() {
        for (SubCommand subCommand : subCommands) {
            subCommand.setCommandManager(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        AbstractCommand commandToExecute = helpCommand;
        try {
            if (args.length > 0) {
                for (AbstractCommand cmd : commands) {
                    if (cmd.isValidTrigger(args)) {
                        commandToExecute = cmd;
                    }
                }
            }


            if (!commandToExecute.hasPermission(commandSender)) {
                MessageHelper.insufficientPermission(plugin, commandSender);
                return true;
            }



            commandToExecute.execute(
                    commandSender,
                    label,
                    Arrays.copyOfRange(args, 0, commandToExecute.getCommands().size()),
                    Arrays.copyOfRange(args, commandToExecute.getCommands().size(), args.length));
            return true;




        } catch (CommandException e) {
            MessageHelper.commandExecutionError(this.plugin, commandSender, e);
            e.printStackTrace();
        } catch (IllegalCommandArgumentException e) {
            MessageHelper.invalidCommand(this.plugin, commandSender);
            MessageHelper.send(this.plugin, commandSender, commandToExecute.getBeautifulHelp());
        }

        //TODO Check & reimplement
//        // was genau wird hier gemacht? optimierungsbedarf?
//        boolean canHelp = false;
//        for (SubCommand subCommand : subCommands) {
//            if (subCommand.isValidHelpTrigger(args)) {
//                MessageHelper.send(this.plugin, commandSender, getHelpForSubCommand(subCommand, commandSender, label));
//                canHelp = true;
//            }
//        }
//        if (canHelp) {
//            return true;
//        }

        // unknown command
        MessageHelper.invalidCommand(this.plugin, commandSender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {

        //TODO Check & reimplement!
        return null;


//        ArrayList<String> completionList = new ArrayList<>();
//
//        for (SubCommand subCommand : subCommands) {
//
//            if (!subCommand.hasPermission(sender)) {
//                continue;
//            }
//
//            String completionCommand = subCommand.getTabCompletionListForArgument(args);
//            if (completionCommand != null) {
//                if (!completionList.contains(completionCommand)) {
//                    if (!args[args.length - 1].equals("")) {
//                        if (completionCommand.startsWith(args[args.length - 1])) {
//                            completionList.add(completionCommand);
//                        }
//                    } else {
//                        completionList.add(completionCommand);
//                    }
//                }
//            }
//
//        }
//
//        return (completionList.isEmpty()) ? null : completionList;
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

    public List<AbstractCommand> getCommands() {
        return commands;
    }

    //endregion
}