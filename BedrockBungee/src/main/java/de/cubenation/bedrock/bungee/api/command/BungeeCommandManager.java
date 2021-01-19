package de.cubenation.bedrock.bungee.api.command;

import de.cubenation.bedrock.core.command.CommandManager;
import net.md_5.bungee.api.CommandSender;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public interface BungeeCommandManager extends CommandManager {

    void execute(CommandSender sender, String[] args);

    Iterable<String> onTabComplete(CommandSender sender, String[] args);
}
