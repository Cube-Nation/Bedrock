package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.exception.NoSuchPluginException;
import de.cubenation.bedrock.helper.Const;
import de.cubenation.bedrock.service.message.MessageService;
import de.cubenation.bedrock.service.permission.PermissionService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public abstract class BasePlugin extends JavaPlugin {

    private Boolean useMessageService = true;

    private PermissionService permissionService;
    private MessageService messageService;

    private String explicitPermissionPrefix;


    public BasePlugin() {
        super();
    }

    @Override
    public final void onEnable() {
        setupConfig();

        onPreEnable();

        initPermissionService();
        initCommands();


        setupPermissionService();
        setupMessageService();

        onPostEnable();
    }

    private void initCommands() {
        if (getCommandManager() != null) {
            for (CommandManager manager : getCommandManager()) {
                manager.getPluginCommand().setExecutor(manager);
                manager.getPluginCommand().setTabCompleter(manager);
            }
        }
    }

    private void setupPermissionService() {
        if (usePermissionService()) {
            permissionService.reloadPermissions();
        }
    }

    private void initPermissionService() {
        if (usePermissionService()) {
            permissionService = new PermissionService(this);
        }
    }

    private void setupMessageService() {

    }

    private void setupConfig() {
        if (getResource("config.yml") == null) {
            log(Level.INFO, "Save default config");
            try {
                saveDefaultConfig();
            } catch (IllegalArgumentException e) {
                File file = new File(
                        this.getDataFolder().getAbsolutePath() +
                                java.lang.System.getProperty("file.separator") +
                                "config.yml");
                try {
                    new YamlConfiguration().save(file.getAbsolutePath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public abstract ArrayList<CommandManager> getCommandManager();


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
        return getFlagColor() + "[" +
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


    public Boolean useMessageService() {
        return useMessageService;
    }

    public void setUseMessageService(Boolean useMessageService) {
        this.useMessageService = useMessageService;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setExplicitPermissionPrefix(String explicitPermissionPrefix) {
        this.explicitPermissionPrefix = explicitPermissionPrefix;
    }

    public String getExplicitPermissionPrefix() {
        if (explicitPermissionPrefix == null) {
            return getName().toLowerCase();
        }
        return explicitPermissionPrefix;
    }

    //endregion


    protected void onPreEnable() {

    }

    protected void onPostEnable() {

    }

    public abstract Boolean usePermissionService();

}
