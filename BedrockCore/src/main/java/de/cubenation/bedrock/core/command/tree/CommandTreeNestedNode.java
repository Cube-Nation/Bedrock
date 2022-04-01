package de.cubenation.bedrock.core.command.tree;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.predefined.HelpCommand;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.ArrayList;
import java.util.logging.Level;

public class CommandTreeNestedNode extends AbstractCommandTreeNestedNode {

    private HelpCommand helpCommand;

    public CommandTreeNestedNode(FoundationPlugin plugin, CommandTreeNode previousNode) {
        super(plugin, previousNode);
    }

    @Override
    protected boolean trySubCommands(BedrockChatSender commandSender, CommandTreePath treePath, String[] args) {
        // TODO: add possibility for empty subcommand
        if (args.length == 0) {
            if (helpCommand == null) {
                return false;
            }
            try {
                this.helpCommand.preExecute(commandSender, treePath, args);
            } catch (Exception e) {
                this.plugin.log(Level.INFO, "Error while executing help command. Shouldn't happen!");
                e.printStackTrace();
            }
            return true;
        }

        // Display help
        ArrayList<JsonMessage> helpList = new ArrayList<>();
//        for (CommandPath possibleHelpCommandPath : this.helpCommand.getHelpCommands()) {
//            if (!(possibleHelpCommandPath.getCommand() instanceof HelpCommand) && possibleHelpCommandPath.isValidHelpTrigger(args)) {
//                helpList.add(possibleHelpCommandPath);
//            }
//        }

        if (helpList.isEmpty()) {
            // Unknown command
            this.plugin.messages().invalidCommand(commandSender);
            return true;
        }

//        this.helpCommand.printHelp(commandSender, args, this.helpCommand.getHelpJsonMessages(commandSender, helpList));

        return false;
    }

    public void addHelpCommand() {
        this.helpCommand = addCommandHandler(HelpCommand.class, "help");
    }
}
