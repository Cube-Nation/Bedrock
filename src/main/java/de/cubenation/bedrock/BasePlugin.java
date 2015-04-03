package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.command.SubCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public class BasePlugin extends JavaPlugin {

    public BasePlugin() {
        super();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public void registerCommand(PluginCommand pluginCommand, SubCommand[] subCommands) {
        CommandManager commandManager = new CommandManager(new ArrayList<SubCommand>(Arrays.asList(subCommands)));

        pluginCommand.setExecutor(commandManager);
        pluginCommand.setTabCompleter(commandManager);
    }


}
