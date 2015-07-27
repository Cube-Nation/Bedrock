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
import java.util.Arrays;
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
     * @param command    the command
     * @param description   the description
     * @param permission the permission
     */
    @SuppressWarnings(value = "unused")
    public UnsortedArgsCommand(final String command, String description, String permission, Argument... arguments) {
        init(new ArrayList<String[]>() {{
            add(new String[]{command});
        }}, description, permission, arguments);

    }

    /**
     * Instantiates a new Command.
     * Each element in commands symbolizes an alias for a command
     * Like '/city teleport' and '/city tp'
     *
     * @param commands   the commands
     * @param description       the description
     * @param permission the permission
     */
    @SuppressWarnings(value = "unused")
    public UnsortedArgsCommand(final String[] commands, String description, String permission, Argument... arguments) {
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
     * @param commands   the commands
     * @param description       the description
     * @param permission the permission
     */
    @SuppressWarnings(value = "unused")
    public UnsortedArgsCommand(ArrayList<String[]> commands, String description, String permission, Argument... arguments) {
        init(commands, description, permission, arguments);
    }


    private void init(ArrayList<String[]> commands, String description, String permission, Argument... arguments) {
        this.commands           = commands;
        this.permissionString   = permission;
        this.description        = description;

        Collections.addAll(this.commandArguments, arguments);
    }


    @Override
    public final ArrayList<String> getTabCompletion(String[] args) {

        System.out.println("args; " + "size: " + args.length + " objects: " + Arrays.toString(args));
        System.out.println("commands; " + "size: " + commands.size() + " objects: " + commands);
        System.out.println("cmd arguments; " + "size: " + commandArguments.size() + " objects: " + commandArguments);


        /*
Wenn /libtest TAB

[16:19:28 INFO]: args; size: 1 objects: []
[16:19:28 INFO]: commands; size: 2 objects: [{set, s}, {home}]
[16:19:28 INFO]: cmd arguments; size: 2 objects: [Argument{key='area', help='Home Punkt', arguments={z=true, y=true, x=true}}, Argument{key='size', help='Reichweite um dich.', arguments={size=true}}]

--> Sollte 'set' voschlagen (s ist kürzer & wird daher nicht zurück gegeben)


[16:31:06 INFO]: args; size: 1 objects: [se]
[16:31:06 INFO]: commands; size: 2 objects: [[Ljava.lang.String;@8df2b24, [Ljava.lang.String;@73894d08]
[16:31:06 INFO]: cmd arguments; size: 2 objects: [Argument{key='area', help='Home Punkt', arguments={z=true, y=true, x=true}}, Argument{key='size', help='Reichweite um dich.', arguments={size=true}}]

--> Sollte 'set' vorschlagen


Wenn libtest Black TAB
[16:22:05 INFO]: args; size: 2 objects: [Blacksheep92, ]
[16:22:05 INFO]: commands; size: 2 objects: [{set, s}, {home}]
[16:22:05 INFO]: cmd arguments; size: 2 objects: [Argument{key='area', help='Home Punkt', arguments={z=true, y=true, x=true}}, Argument{key='size', help='Reichweite um dich.', arguments={size=true}}]

--> Sollte 'home' voschlagen
         */


        // TODO fast return
        // if () return null


        if (commands.size() >= args.length) {
            //Simple, just check each command
            return getTabCompletionFromCommands(args);
        }


        for (int i = 0; i < args.length; i++) {


            //Wenn args[i] == "" return alle Validen Args an dieser Stelle
            // else substring

            //Wenn alle eines commands/args nicht passen direkt return

            boolean valid = false;

            if (i < commands.size()) {
                // First check all subcommands
                for (String com : commands.get(i)) {
                    if (com.startsWith(args[i])) {
                        valid = true;
                    }
                }
            }
            // else commandsize+argslength krams


        }


        // Then, check all arguments


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
//            return list.get(0);
//        }
        return null;
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
