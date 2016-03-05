package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.manager.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

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
}