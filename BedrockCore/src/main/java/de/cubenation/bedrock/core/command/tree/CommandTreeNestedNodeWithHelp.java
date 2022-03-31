package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.CommandPath;
import de.cubenation.bedrock.core.command.predefined.HelpCommand;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.ArrayList;
import java.util.logging.Level;

public class CommandTreeNestedNodeWithHelp extends AbstractCommandTreeNestedNode {

    private final HelpCommand helpCommand;

    public CommandTreeNestedNodeWithHelp(FoundationPlugin plugin, String label, CommandTreeNode previousNode) {
        super(plugin, label, previousNode);

        // TODO: remove null
        this.helpCommand = new HelpCommand(plugin, "help", this);
        addCommandHandler(helpCommand, "help");
    }

    @Override
    protected boolean trySubCommands(BedrockChatSender commandSender, String[] args) {
        // TODO: add possibility for empty subcommand
        if (args.length == 0) {
            try {
                this.helpCommand.preExecute(commandSender, args);
            } catch (Exception e) {
                this.plugin.log(Level.INFO, "Error while executing help command. Shouldn't happen!");
                e.printStackTrace();
            }
            return true;
        }

        // Display help
        ArrayList<CommandPath> helpList = new ArrayList<>();
        for (CommandPath possibleHelpCommandPath : this.helpCommand.getHelpCommands()) {
            if (!(possibleHelpCommandPath.getCommand() instanceof HelpCommand) && possibleHelpCommandPath.isValidHelpTrigger(args)) {
                helpList.add(possibleHelpCommandPath);
            }
        }

        if (helpList.isEmpty()) {
            // Unknown command
            this.plugin.messages().invalidCommand(commandSender);
            return true;
        }

        this.helpCommand.printHelp(commandSender, args, this.helpCommand.getHelpJsonMessages(commandSender, helpList));
        return true;
    }
}
