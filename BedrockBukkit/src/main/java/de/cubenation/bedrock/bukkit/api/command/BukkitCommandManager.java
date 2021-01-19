package de.cubenation.bedrock.bukkit.api.command;

import de.cubenation.bedrock.core.command.CommandManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public interface BukkitCommandManager extends CommandManager, CommandExecutor, TabCompleter {
}
