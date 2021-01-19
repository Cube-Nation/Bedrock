package de.cubenation.bedrock.core.service.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.AbstractCommand;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.Arrays;
import java.util.List;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public abstract class SimpleCommandManager implements CommandManager {

    private FoundationPlugin plugin;

    private String label;

    private AbstractCommand command;

    public SimpleCommandManager(FoundationPlugin plugin, String label) {
        this.plugin = plugin;
        this.label = label;
    }

    @Override
    public boolean onCommand(BedrockChatSender commandSender, String[] args) {
        // Try command
        if (command.tryCommand(commandSender, args))
            return true;

        // Unknown command
        plugin.messages().invalidCommand(commandSender);

        JsonMessage jsonHelp = command.getJsonHelp(commandSender);
        if (jsonHelp == null) {
            plugin.messages().insufficientPermission(commandSender);
        } else {
            jsonHelp.send(commandSender);
        }
        return true;
    }

    @Override
    public List<String> onAutoComplete(BedrockChatSender sender, String[] args) {
        return null;
    }

    public void setCommand(AbstractCommand command) {
        this.command = command;
    }

    //region Getter
    public FoundationPlugin getPlugin() {
        return plugin;
    }

    public String getLabel() {
        return label;
    }

    public List<AbstractCommand> getCommands() {
        return Arrays.asList(new AbstractCommand[]{command});
    }

    //endregion

}
