package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.argument.CommandArguments;
import de.cubenation.bedrock.command.argument.UnsortedArgument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class UnsortedArgsCommand extends AbstractCommand {

    private ArrayList<String[]> commands;
    private CommandArguments commandArguments = new CommandArguments();
    private String label;
    private String description;
    private Permission permission;
    private CommandManager commandManager;
    private String permissionString;
    protected BasePlugin plugin;


    /**
     * Instantiates a new Command with just one command
     * Like '/city help'
     *
     * @param command     the command
     * @param description the description
     * @param permission  the permission
     */
    @SuppressWarnings(value = "unused")
    public UnsortedArgsCommand(final String command, String description, String permission, UnsortedArgument... arguments) {
        init(new ArrayList<String[]>() {{
            add(new String[]{command});
        }}, description, permission, arguments);

    }

    /**
     * Instantiates a new Command.
     * Each element in commands symbolizes an alias for a command
     * Like '/city teleport' and '/city tp'
     *
     * @param commands    the commands
     * @param description the description
     * @param permission  the permission
     */
    @SuppressWarnings(value = "unused")
    public UnsortedArgsCommand(final String[] commands, String description, String permission, UnsortedArgument... arguments) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(commands);

        init(list, description, permission, arguments);
    }

    /**
     * Instantiates a new Command.
     * Each element in <code>commands</code> symbolizes a new command
     * Like '/city set bonus'
     * The String[] contains a single command or aliases
     * Like '/city set bonus', '/city s bonus', /
     *
     * @param commands    the commands
     * @param description the description
     * @param permission  the permission
     */
    @SuppressWarnings(value = "unused")
    public UnsortedArgsCommand(ArrayList<String[]> commands, String description, String permission, UnsortedArgument... arguments) {
        init(commands, description, permission, arguments);
    }


    private void init(ArrayList<String[]> commands, String description, String permission, UnsortedArgument... arguments) {
        this.commands = commands;
        this.permissionString = permission;
        this.description = description;

        Collections.addAll(this.commandArguments, arguments);
    }


    @Override
    public final ArrayList<String> getTabCompletion(String[] args) {
        if (commands.size() >= args.length) {
            return getTabCompletionFromCommands(args);
        } else if (args.length > commands.size()) {
            return getTabCompletionFromArguments(args);
        }
        return null;
    }

    private ArrayList<String> getTabCompletionFromArguments(String[] args) {
        if (!checkCommands(args)) {
            return null;
        }

        // TODO
        // Unschön, etwas besseres überlegen
        ArrayList<UnsortedArgument> cmdArgs = new ArrayList<>();
        for (int i = 0; i < getCommandArguments().size(); i++) {
            if (getCommandArguments().get(i) instanceof UnsortedArgument) {
                cmdArgs.add((UnsortedArgument) getCommandArguments().get(i));
            }
        }

        ArrayList<UnsortedArgument> recursive = getPossibleArguments(cmdArgs, args, commands.size());

        ArrayList<String> arrayList = new ArrayList<>();
        if (recursive != null) {
            for (UnsortedArgument argument : recursive) {
                if (argument.getKey().startsWith(args[args.length - 1])) {
                    arrayList.add(argument.getKey());
                }
            }
        }

        return arrayList;
    }


    private ArrayList<UnsortedArgument> getPossibleArguments(ArrayList<UnsortedArgument> list, String[] args, int position) {
        if (position >= args.length) {
            return null;
        } else if (position == args.length - 1) {
            return list;
        }

        UnsortedArgument argument = containsKey(list, args[position]);
        if (argument != null) {
            list.remove(argument);
            // Add this argument
            position++;
            // Add Placeholder size to ignore them
            position += argument.getPlaceholder().size();
            return getPossibleArguments(list, args, position);
        }
        return null;
    }

    private UnsortedArgument containsKey(ArrayList<UnsortedArgument> list, String key) {
        for (UnsortedArgument argument : list) {
            if (argument.getKey().startsWith(key)) {
                return argument;
            }
        }
        return null;
    }


    private boolean checkCommands(String[] args) {

        for (int i = 0; i < commands.size(); i++) {

            boolean validCommand = false;
            for (String com : getCommands().get(i)) {
                if (com.startsWith(args[i])) {
                    validCommand = true;
                }
            }
            if (!validCommand) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns if the subcommand is a valid trigger.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    @Override
    public final boolean isValidTrigger(String[] args) {

        if (args.length >= commands.size()) {
            // Check previous Arguments
            boolean prevResult = true;
            for (int i = 0; i < commands.size(); i++) {
                boolean res = false;
                for (String com : commands.get(i)) {
                    if (args[i].equalsIgnoreCase(com)) {
                        res = true;
                    }
                }
                if (!res) {
                    prevResult = false;
                }
            }
            return prevResult;
        }
        // Not enough arguments
        // TODO: Should display if not enough arguments available!
        return false;
    }

    public TextComponent getBeautifulHelp(CommandSender sender) {
        return MessageHelper.getHelpForSubCommand(plugin, sender, this);
    }


    @Override
    public ArrayList<String[]> getCommands() {
        return commands;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public CommandArguments getCommandArguments() {
        return commandArguments;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Permission getPermission() {
        return permission;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public String getPermissionString() {
        return permissionString;
    }

    @Override
    public BasePlugin getPlugin() {
        return plugin;
    }


    @Override
    public void setCommands(ArrayList<String[]> commands) {
        this.commands = commands;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }

    @Override
    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}