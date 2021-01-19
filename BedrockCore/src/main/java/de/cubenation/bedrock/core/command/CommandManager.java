package de.cubenation.bedrock.core.command;

import de.cubenation.bedrock.core.FoundationPlugin;

import java.util.List;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public interface CommandManager extends CommandExecutor, AutoCompletionExecutor {

    FoundationPlugin getPlugin();

    String getLabel();

    List<AbstractCommand> getCommands();
}
