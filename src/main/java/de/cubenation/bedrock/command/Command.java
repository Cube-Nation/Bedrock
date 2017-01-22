package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.manager.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public abstract class Command extends AbstractCommand {

    public Command(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public ArrayList<String> getTabCompletion(String[] args, CommandSender sender) {
        ArrayList<String> tabCompletionFromCommands = getTabCompletionFromCommands(args);
        if (isValidTrigger(args)) {
            if (args != null && subcommands != null) {
                ArrayList<String> tabArgumentCompletion = getTabArgumentCompletion(sender,args.length - subcommands.size() - 1, Arrays.copyOfRange(args, subcommands.size(), args.length));
                if (tabArgumentCompletion != null && !tabArgumentCompletion.isEmpty()) {
                    if (tabCompletionFromCommands == null) {
                        tabCompletionFromCommands = new ArrayList<>();
                    }
                    tabCompletionFromCommands.addAll(tabArgumentCompletion);
                }
            }


            if (tabCompletionFromCommands == null || args == null) {
                return tabCompletionFromCommands;
            }

            ArrayList<String> toRemove = new ArrayList<>();
            String arg = args[args.length - 1];

            for (String completion: tabCompletionFromCommands) {
                if (!completion.startsWith(arg)) {
                    toRemove.add(completion);
                }
            }

            tabCompletionFromCommands.removeAll(toRemove);
        }

        return tabCompletionFromCommands;
    }

    /**
     * Returns if the subcommand is a valid trigger.
     *
     * @param args the args
     * @return true if it is a valid trigger, else false
     */
    @Override
    public boolean isValidTrigger(String[] args) {

        if (args.length >= this.subcommands.size()) {
            // Check previous Arguments
            boolean prevResult = true;
            for (int i = 0; i < this.subcommands.size(); i++) {
                boolean res = false;
                for (String com : this.subcommands.get(i)) {
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

    @Deprecated
    public ArrayList<String> getTabArgumentCompletion(int argumentIndex, String[] args) {
        return null;
    }

    public ArrayList<String> getTabArgumentCompletion(CommandSender sender, int argumentIndex, String[] args) {
        return getTabArgumentCompletion(argumentIndex, args);
    }

}