package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class Command extends AbstractCommand {

    private ArrayList<String[]> commands;
    private String description;
    private String label;
    private Permission permission;
    private CommandManager commandManager;
    private String permissionString;
    protected BasePlugin plugin;


    /**
     * Instantiates a new Command with just one command
     * Like '/city help'
     *
     * @param command       the command
     * @param description   the description
     * @param permission    the permission
     */
    @SuppressWarnings(value = "unused")
    public Command(final String command, String description, String permission) {
        init(new ArrayList<String[]>() {{
            add(new String[]{command});
        }}, description, permission);
    }

    /**
     * Instantiates a new Command.
     * Each element in commands symbolizes an alias for a command
     * Like '/city teleport' and '/city tp'
     *
     * @param commands      the commands
     * @param description   the description
     * @param permission    the permission
     */
    @SuppressWarnings(value = "unused")
    public Command(final String[] commands, String description, String permission) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(commands);

        init(list, description, permission);
    }

    /**
     * Instantiates a new Command.
     * Each element in <code>commands</code> symbolizes a new command
     * Like '/city set bonus'
     * The String[] contains a single command or aliases
     * Like '/city set bonus', '/city s bonus', /
     *
     * @param commands      the commands
     * @param description   the description
     * @param permission    the permission
     */
    @SuppressWarnings(value = "unused")
    public Command(ArrayList<String[]> commands, String description, String permission) {
        init(commands, description, permission);
    }

    private void init(ArrayList<String[]> commands, String description, String permission) {
        this.commands           = commands;
        this.permissionString   = permission;
        this.description        = description;
    }


    @Override
    public final ArrayList<String> getTabCompletion(String[] args) {
        return getTabCompletionFromCommands(args);
//        if (commands.size() >= args.length) {
//            for (int i = 0; i < args.length; i++) {
//                boolean result;
//                if (!args[i].equals("")) {
//                    result = false;
//                    for (String com : commands.get(i)) {
//                        if (com.startsWith(args[i])) {
//                            result = true;
//                        }
//                    }
//                } else {
//                    result = true;
//                }
//
//                if (!result) {
//                    return null;
//                }
//            }
//
//            ArrayList<String> list = new ArrayList<>(Arrays.asList(commands.get(args.length - 1)));
//
//            Collections.sort(list, new Comparator<String>() {
//                @Override
//                public int compare(String s1, String s2) {
//                    return s2.compareToIgnoreCase(s1);
//                }
//            });
//
//            // Just return the "largets" command for completion to help the user to choose the right.
//
//            //TODO Better implementation available
//            ArrayList<String> cmd = new ArrayList<>();
//            cmd.add(list.get(0));
//            return cmd;
//        }
//        return null;
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

    @Override
    public TextComponent getBeautifulHelp(CommandSender sender) {
        return MessageHelper.getHelpForSubCommand(plugin, sender, this);
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
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
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
