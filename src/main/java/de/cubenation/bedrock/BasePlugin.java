package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.helper.Const;
import de.cubenation.bedrock.service.message.MessageService;
import de.cubenation.bedrock.service.permission.PermissionService;
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
public abstract class BasePlugin extends JavaPlugin  {

    private Boolean usePermissionService = true;
    private Boolean useMessageService = true;

    private PermissionService permissionService;
    private MessageService messageService;

    public BasePlugin() {
        super();
    }

    @Override
    public final void onEnable() {
        onPreEnable();

        setupConfig();

        setupPermissionService();
        setupMessageService();

        log(Level.INFO, "version " + getDescription().getVersion() + " enabled");

        onPostEnable();
    }

    private void setupPermissionService() {

    }

    private void setupMessageService() {

    }


    private void setupConfig() {
        if (getResource("config.yml") != null) {
            getLogger().info("save default config");
            saveDefaultConfig();
        }
    }



    public void registerCommand(BasePlugin plugin, PluginCommand pluginCommand, SubCommand[] subCommands) {
        addCommandManager(plugin, null, pluginCommand, subCommands);
    }

    public void registerCommand(BasePlugin plugin, String helpPrefix, PluginCommand pluginCommand, SubCommand[] subCommands) {
        addCommandManager(plugin, helpPrefix, pluginCommand, subCommands);
    }

    private void addCommandManager(BasePlugin plugin, String helpPrefix, PluginCommand pluginCommand, SubCommand[] subCommands) {
        CommandManager commandManager = new CommandManager(plugin, helpPrefix, new ArrayList<SubCommand>(Arrays.asList(subCommands)));

        pluginCommand.setExecutor(commandManager);
        pluginCommand.setTabCompleter(commandManager);

        commandManagers.add(commandManager);
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

    public String getString(String key) {
        if (!useMessageService()) {
            return Const.NO_MESSAGE_SERVICE;
        }

        if (messageService == null) {
            return Const.NO_MESSAGE_SERVICE;
        } else {
            return messageService.getString(key);
        }
    }


    //region Getter/Setter
    public Boolean usePermissionService() {
        return usePermissionService;
    }

    public void setUsePermissionService(Boolean usePermissionService) {
        this.usePermissionService = usePermissionService;
    }

    public Boolean useMessageService() {
        return useMessageService;
    }

    public void setUseMessageService(Boolean useMessageService) {
        this.useMessageService = useMessageService;
    }
    //endregion



    public void onPreEnable() {

    }

    public void onPostEnable() {

    }

}
