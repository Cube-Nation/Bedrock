package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public class BasePlugin extends JavaPlugin  {

    public BasePlugin() {
        super();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public void registerCommand(BasePlugin plugin, PluginCommand pluginCommand, SubCommand[] subCommands) {
        CommandManager commandManager = new CommandManager(plugin, new ArrayList<SubCommand>(Arrays.asList(subCommands)));

        pluginCommand.setExecutor(commandManager);
        pluginCommand.setTabCompleter(commandManager);
    }

    public void registerCommand(BasePlugin plugin, String helpPrefix, PluginCommand pluginCommand, SubCommand[] subCommands) {
        CommandManager commandManager = new CommandManager(plugin, helpPrefix, new ArrayList<SubCommand>(Arrays.asList(subCommands)));

        pluginCommand.setExecutor(commandManager);
        pluginCommand.setTabCompleter(commandManager);
    }

    public JavaPlugin getPlugin(String name) throws NoSuchPluginException {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(name);
        if (plugin == null) {
            throw new NoSuchPluginException(name);
        }
        return plugin;
    }

    public void log(Level level, String message) {
        Logger.getLogger("Minecraft").log(
                level,
                ChatColor.stripColor(String.format("%s %s", this.getMessagePrefix(), message))
        );
    }

    public void disable(Exception e) {
        log(Level.SEVERE, "Unrecoverable error: " + e.getMessage());
        log(Level.SEVERE, "Disabling plugin");
        this.getPluginLoader().disablePlugin(this);
    }

    public void disable(Exception e, CommandSender sender) {
        sender.sendMessage(this.getMessagePrefix() + "Unrecoverable error. Disabling plugin");
        this.disable(e);
    }


    public String getMessagePrefix() {
        return 	getFlagColor() + "[" +
                getPrimaryColor() + this.getDescription().getName() +
                getFlagColor() + "]" +
                ChatColor.RESET;
    }

    public ChatColor getPrimaryColor() {
        return ChatColor.AQUA;
    }

    public ChatColor getSecondaryColor() {
        return ChatColor.BLUE;
    }

    public ChatColor getFlagColor() {
        return ChatColor.GRAY;
    }

}
