package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class KeyValueCommand extends AbstractCommand {

    private ArrayList<String[]> commands;
    private ArrayList<Argument> arguments = new ArrayList<>();
    private String label;
    private String[] help = new String[]{};
    private Permission permission;
    private CommandManager commandManager;
    private String permissionString;
    protected BasePlugin plugin;


    /**
     * Instantiates a new Command with just one command
     * Like '/city help'
     *
     * @param command    the command
     * @param help       the help
     * @param permission the permission
     */
    @SuppressWarnings(value = "unused")
    public KeyValueCommand(final String command, String[] help, String permission, Argument... arguments) {
        init(new ArrayList<String[]>() {{
            add(new String[]{command});
        }}, help, permission, arguments);

    }

    /**
     * Instantiates a new Command.
     * Each element in commands symbolizes an alias for a command
     * Like '/city teleport' and '/city tp'
     *
     * @param commands   the commands
     * @param help       the help
     * @param permission the permission
     */
    @SuppressWarnings(value = "unused")
    public KeyValueCommand(final String[] commands, String[] help, String permission, Argument... arguments) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(commands);

        init(list, help, permission, arguments);
    }

    /**
     * Instantiates a new Command.
     * Each element in <code>commands</code> symbolizes a new command
     * Like '/city set bonus'
     * The String[] contains a single command or aliases
     * Like '/city set bonus', '/city s bonus', /
     *
     * @param commands   the commands
     * @param help       the help
     * @param permission the permission
     */
    @SuppressWarnings(value = "unused")
    public KeyValueCommand(ArrayList<String[]> commands, String[] help, String permission, Argument... arguments) {
        init(commands, help, permission, arguments);
    }


    private void init(ArrayList<String[]> commands, String[] help, String permission, Argument... arguments) {
        this.commands = commands;
        this.permissionString = permission;
        this.help = help;
        for (Argument argument : arguments) {
            this.arguments.add(argument);
        }
    }


    @Override
    public final ArrayList<String> getTabCompletion(String[] args) {

        System.out.println("args; " + "size: " + args.length + " objects: " + Arrays.toString(args));
        System.out.println("commands; " + "size: " + commands.size() + " objects: " + commands);
        System.out.println("cmd arguments; " + "size: " + arguments.size() + " objects: " + arguments);


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

    private void getTabArgumentList() {
        ArrayList<ArrayList<String>> arguments = new ArrayList<>();

        // All Commands like {0={s,set},1={home}}
        for (int i = 0; i < getCommands().size(); i++) {
            if (arguments.get(i) == null) {
                arguments.set(i, new ArrayList<String>());
            }
            ArrayList<String> listAtIndex = arguments.get(i);
            Collections.addAll(listAtIndex, getCommands().get(i));
        }

        // All Arguments like {2={area,point}, 3={}, 4={point}, 6={area}}

        for (int i = getCommands().size(); i < arguments.size() + getCommands().size(); i++) {
            if (arguments.get(i) == null) {
                arguments.set(i, new ArrayList<String>());
            }
            ArrayList<String> listAtIndex = arguments.get(i);


            Collections.addAll(listAtIndex, getCommands().get(i));
        }

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

    public TextComponent getBeautifulHelp() {
        return MessageHelper.getHelpForSubCommand(plugin, this);
    }

    /**
     * Gets arguments help.
     * The Key describes the Argument. For example {value}.
     * {} - means required.
     * [] - means optional.
     * <p/>
     * The Value is the help for the Argument.
     *
     * @return the arguments help
     */
    public abstract LinkedHashMap<String, String> getArguments();

    public ArrayList<String> getArgumentsHelp() {
        return null;
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
    public String[] getHelp() {
        return help;
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
    public void setHelp(String[] help) {
        this.help = help;
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
