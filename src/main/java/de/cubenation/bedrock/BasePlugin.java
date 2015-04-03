package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.command.SubCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public abstract class BasePlugin extends JavaPlugin {

    public void registerCommand(String command, SubCommand[] subCommands) {
        CommandManager commandManager = new CommandManager(new ArrayList<SubCommand>(Arrays.asList(subCommands)));

        getCommand(command).setExecutor(commandManager);
        getCommand(command).setTabCompleter(commandManager);
    }


}
