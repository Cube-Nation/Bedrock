package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends BedrockSubCommand {

    private List<BedrockSubCommand> subCommands;

    public HelpCommand(String name, List<String> help) {
        super(name, help);
    }


    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {
        sender.sendMessage("Should display fancy help");
        for (BedrockSubCommand subCommand : subCommands) {
            sender.sendMessage(subCommand.getName() + " - " + subCommand.getHelp().toString());
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
}
