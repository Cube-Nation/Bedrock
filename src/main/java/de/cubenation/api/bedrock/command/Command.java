package de.cubenation.api.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class Command extends AbstractCommand {

    public Command(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public ArrayList<String> getTabCompletion(String[] args, CommandSender sender) {
        ArrayList<String> tabCompletionFromCommands = getTabCompletionFromCommands(args);
        if (isValidTrigger(args)) {
            if (args != null && this.getSubcommands()!= null) {
                ArrayList<String> tabArgumentCompletion = getTabArgumentCompletion(sender,args.length - this.getSubcommands().size() - 1, Arrays.copyOfRange(args, this.getSubcommands().size(), args.length));
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
                if (!completion.toLowerCase().startsWith(arg.toLowerCase())) {
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
     * @return true if it is a valid trigger, false otherwise
     */
    @Override
    public boolean isValidTrigger(String[] args) {
        return this.isMatchingSubCommands(args);
    }

    public ArrayList<String> getTabArgumentCompletion(CommandSender sender, int argumentIndex, String[] args) {
        return null;
    }

}