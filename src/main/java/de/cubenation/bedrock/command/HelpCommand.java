package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends SubCommand {

    private List<SubCommand> subCommands;

    public HelpCommand() {
        super("help", new String[]{"Hilfe"}, null);
    }


    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {
        sender.sendMessage("Should display fancy help");
        for (SubCommand subCommand : subCommands) {
            sender.sendMessage(subCommand.getName() + " - " + subCommand.getHelp());
        }
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public String getArgumentsHelp() {
        return null;
    }

    @Override
    public HashMap<Integer, SubCommand[]> getSubcommands() {
        return null;
    }

}
