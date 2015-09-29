package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.argument.CommandArguments;
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
public abstract class Command extends AbstractCommand {

    private ArrayList<String[]> commands;
    private String label;
    private String description;
    private CommandArguments commandArguments = new CommandArguments();
    private ArrayList<Permission> permissions;
    private CommandManager commandManager;
    private ArrayList<String> permissionStrings;
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
    public Command(final String command, String description, final String permission, Argument... arguments) {
        init(
                new ArrayList<String[]>() {{
                    add(new String[]{command});
                }},
                description,
                new ArrayList() {{
                    add(permission);
                }},
                arguments);
    }

    /**
     * Instantiates a new Command with just one command
     * Like '/city help'
     *
     * @param command     the command
     * @param description the description
     * @param permissions the permissions
     */
    @SuppressWarnings(value = "unused")
    public Command(final String command, String description, ArrayList<String> permissions, Argument... arguments) {
        init(
                new ArrayList<String[]>() {{
                    add(new String[]{command});
                }},
                description,
                permissions,
                arguments);
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
    public Command(final String[] commands, String description, final String permission, Argument... arguments) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(commands);

        init(list, description, new ArrayList() {{
            add(permission);
        }}, arguments);
    }

    /**
     * Instantiates a new Command.
     * Each element in commands symbolizes an alias for a command
     * Like '/city teleport' and '/city tp'
     *
     * @param commands    the commands
     * @param description the description
     * @param permissions the permissions
     */
    @SuppressWarnings(value = "unused")
    public Command(final String[] commands, String description, ArrayList<String> permissions, Argument... arguments) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(commands);

        init(list, description, permissions, arguments);
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
    public Command(ArrayList<String[]> commands, String description, final String permission, final Argument... arguments) {
        init(commands, description, new ArrayList() {{
            add(permission);
        }}, arguments);
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
     * @param permissions the permissions
     */
    @SuppressWarnings(value = "unused")
    public Command(ArrayList<String[]> commands, String description, ArrayList<String> permissions, Argument... arguments) {
        init(commands, description, permissions, arguments);
    }

    private void init(ArrayList<String[]> commands, String description, ArrayList permissions, Argument[] arguments) {
        this.commands = commands;
        this.permissionStrings = permissions;
        this.description = description;
        Collections.addAll(commandArguments, arguments);
    }


    @Override
    public ArrayList<String> getTabCompletion(String[] args, CommandSender sender) {
        return getTabCompletionFromCommands(args);
    }

    /**
     * Returns if the subcommand is a valid trigger.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    @Override
    public boolean isValidTrigger(String[] args) {

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

    @Override
    public TextComponent getBeautifulHelp(CommandSender sender) {
        return MessageHelper.getHelpForSubCommand(plugin, sender, this);
    }

    @Override
    public CommandArguments getCommandArguments() {
        return commandArguments;
    }

    @Override
    public ArrayList<String[]> getCommands() {
        return commands;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public ArrayList<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public ArrayList<String> getPermissionStrings() {
        return permissionStrings;
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
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void addPermission(Permission permission) {
        if (this.permissions == null) {
            this.permissions = new ArrayList<>();
        }

        this.permissions.add(permission);
    }

    @Override
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}
