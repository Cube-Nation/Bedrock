package de.cubenation.bedrock.core.service.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public abstract class SimpleCommandManager implements CommandManager {

    private final FoundationPlugin plugin;

    private final String label;

    private Command command;

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
        ArrayList<String> list = new ArrayList<>();
        if (!this.command.hasPermission(sender)) {
            return null;
        }

        List<String> autoCompletion = this.command.getAutoCompletion(args, sender);
        if (autoCompletion != null) {
            list.addAll(autoCompletion);
        }

        // Remove duplicates
        Set<String> set = new HashSet<>(list);
        if (set.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(set);
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    //region Getter
    public FoundationPlugin getPlugin() {
        return plugin;
    }

    public String getLabel() {
        return label;
    }

    public List<Command> getCommands() {
        return List.of(command);
    }

    //endregion

}
