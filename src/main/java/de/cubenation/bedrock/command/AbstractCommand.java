package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.translation.Translation;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class AbstractCommand {

    /**
     *
     * @param sender the sender of the command
     * @param label the label of the command
     * @param subcommands the list of subcommands
     * @param args the list of arguments
     * @throws CommandException
     * @throws IllegalCommandArgumentException
     */
    public abstract void execute(CommandSender sender,
                                 String label,
                                 String[] subcommands,
                                 String[] args) throws CommandException, IllegalCommandArgumentException;

    /**
     * Gets tab completion for argument.
     *
     * @param args the args of the asking command
     * @return the tab completion for argument
     */
    public abstract ArrayList<String> getTabCompletion(String[] args);

    public final ArrayList<String> getTabCompletionFromCommands(String[] args) {
        if (getCommands().size() >= args.length) {
            for (int i = 0; i < args.length; i++) {

                boolean validCommand = false;
                for (String com : getCommands().get(i)) {
                    if (com.startsWith(args[i])) {
                        validCommand = true;
                    }
                }
                if (!validCommand) {
                    return null;
                }
            }

            final ArrayList<String> list = new ArrayList<>(Arrays.asList(getCommands().get(args.length - 1)));

            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s2.compareToIgnoreCase(s1);
                }
            });

            // Just return the "largets" command for completion to help the user to choose the right.
            final String completionCommand = list.get(0);

            return new ArrayList<String>(){{add(completionCommand);}};
        } else {
            System.out.println(" NOT (getCommands().size() >= args.length)  --> Returning NULL");
            return null;
        }
    }

    /**
     * Returns if the subcommand is a valid trigger for the asking command.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    public abstract boolean isValidTrigger(String[] args);

    //TODO: Check if needed.
//    /**
//     * Gets arguments help.
//     *
//     * @return the arguments help
//     */
//    public abstract ArrayList<String> getArgumentsHelp();


    //TODO: D1rty - Should return some TextComponenent Msg?
    public abstract TextComponent getBeautifulHelp();


    /**
     * Returns if the Sender has permission.
     *
     * @param sender the sender
     * @return true, if the sender has Permissions, else false.
     */
    public final boolean hasPermission(CommandSender sender) {
        return getPermission() != null && getPermission().userHasPermission(sender);
    }

    /**
     * Add CommandManager, set BasePlugin instance and call setup()
     * @param commandManager the CommandManager of this command
     */
    public final void addCommandManager(CommandManager commandManager) {
        setCommandManager(commandManager);
        setPlugin(commandManager.getPlugin());

        setup();
    }


    /**
     * Use this method to init some stuff.
     * It's called after BasePlugin instance 'plugin' is set.
     */
    public void setup() {

        if (getPlugin() == null) {
            throw new RuntimeException("BasePlugin instance isn't availabe!");
        }

        setPermission(new Permission(getPermissionString(), getPlugin()));

        // assign help strings
        List<String> help_strings = new ArrayList<>();
        for (String h : getHelp()) {

            // FIXME: D1rty move this to Translation class

            // a help message *should* come from the plugin that uses it, so we try to take it from there
            String foreign_plugin_help = new Translation(getPlugin(), h).getTranslation();
            if (!foreign_plugin_help.equals("")) {
                help_strings.add(foreign_plugin_help);
                continue;
            }

            // should be a Bedrock built-in command, take it from Bedrocks' locale file
            help_strings.add(new Translation(BedrockPlugin.getInstance(), h).getTranslation());
        }

        setHelp(help_strings.toArray(new String[help_strings.size()]));

    }


    public abstract ArrayList<String[]> getCommands();

    public abstract String[] getHelp();

    public abstract Permission getPermission();

    public abstract CommandManager getCommandManager();

    public abstract String getPermissionString();

    public abstract BasePlugin getPlugin();


    public abstract void setCommands(ArrayList<String[]> commands);

    public abstract void setHelp(String[] help);

    public abstract void setPermission(Permission permission);

    public abstract void setCommandManager(CommandManager commandManager);

    public abstract void setPermissionString(String permissionString);

    public abstract void setPlugin(BasePlugin plugin);
}
