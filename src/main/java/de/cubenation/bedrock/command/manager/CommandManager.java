package de.cubenation.bedrock.command.manager;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.predefined.HelpCommand;
import de.cubenation.bedrock.command.predefined.PermissionCommand;
import de.cubenation.bedrock.command.predefined.ReloadCommand;
import de.cubenation.bedrock.command.predefined.VersionCommand;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.MessageHelper;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.*;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private BasePlugin plugin;

    private PluginCommand pluginCommand;

    private List<AbstractCommand> commands = new ArrayList<>();

    private HelpCommand helpCommand;


    public CommandManager(BasePlugin plugin, PluginCommand pluginCommand, String helpPrefix, ArrayList<AbstractCommand> commands) {

        this.plugin = plugin;
        this.pluginCommand = pluginCommand;

        for (AbstractCommand command : commands) {
            this.commands.add(command);
        }

        this.helpCommand = new HelpCommand(this, helpPrefix);

        this.commands.add(helpCommand);
        helpCommand.setHelpPrefix(helpPrefix);

        // add default commands that all plugins are capable of
        this.commands.add(new PermissionCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new VersionCommand());

        if (this.commands != null) {
            for (AbstractCommand command : this.commands) {
                command.addCommandManager(this);
            }
        }
    }


    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        AbstractCommand commandToExecute = helpCommand;
        try {
            if (args.length <= 0) {
                commandToExecute.execute(commandSender, label, null, args);
                return true;
            } else {
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

            TextComponent component_help = commandToExecute.getBeautifulHelp(commandSender);
            if (component_help == null)
                MessageHelper.insufficientPermission(this.plugin, commandSender);
            else
                MessageHelper.send(this.plugin, commandSender, component_help);
        }

        // unknown command
        MessageHelper.invalidCommand(this.plugin, commandSender);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (AbstractCommand cmd : getCommands()) {
            if (!cmd.hasPermission(sender)) {
                continue;
            }

            ArrayList tabCom = cmd.getTabCompletion(args, sender);
            if (tabCom != null) {
                list.addAll(tabCom);
            }
        }
        // Remove duplicates.
        Set<String> set = new HashSet<>(list);
        if (set.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(set);
        }
    }


    //region Getter
    public BasePlugin getPlugin() {
        return plugin;
    }

    public PluginCommand getPluginCommand() {
        return pluginCommand;
    }

    public List<AbstractCommand> getCommands() {
        return commands;
    }

    //endregion
}