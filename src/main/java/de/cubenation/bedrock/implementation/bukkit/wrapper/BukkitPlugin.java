package de.cubenation.bedrock.implementation.bukkit.wrapper;

import de.cubenation.bedrock.core.BasePlugin;
import de.cubenation.bedrock.core.exception.NoSuchPluginException;
import de.cubenation.bedrock.implementation.bukkit.BukkitBasePlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BukkitPlugin implements BasePlugin {

    JavaPlugin plugin;

    public BukkitPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static BukkitPlugin wrap(JavaPlugin plugin) {
        return new BukkitPlugin(plugin);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the plugin name as a string.
     * <p>
     *
     * @return The plugin name
     */
    @Override
    public String getPrettyName() {
        return plugin.getDescription().getName();
    }

    /**
     * Returns the plugin version as a string.
     * <p>
     *
     * @return The plugin version
     */
    @Override
    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Returns a string of the current plugin, known as the message prefix.
     * <p>
     *
     * @return The message prefix
     */
    public String getMessagePrefix() {
        return this.getMessagePrefix(this);
    }

    /**
     * Returns a string of a given plugin, known as the message prefix.
     * <p>
     *
     * @param plugin The plugin
     * @return The message prefix
     */
    public <P extends BasePlugin> String getMessagePrefix(P plugin) {
        return this.getMessagePrefix(getPrettyName());
    }

    /**
     * Returns a string with the plugin name, known as the message prefix.
     * <p>
     *
     * @param plugin The plugin name
     * @return The message prefix
     */
    public String getMessagePrefix(String plugin) {
        return "[" + plugin + "]";
    }

    /**
     * Log a message with a given log level to the Minecraft logfile
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @see Level
     */
    public void log(Level level, String message) {
        Logger.getLogger("Minecraft").log(
                level,
                ChatColor.stripColor(String.format("%s %s", this.getMessagePrefix(), message))
        );
    }

    /**
     * Returns the Logger of the plugin
     * @return The plugin Logger
     */
    public Logger getLogger() {
        return plugin.getLogger();
    }

    /**
     * Log a message with a given log level to the Minecraft logfile.
     * The stacktrace of the Throwable object is printed to STDOUT.
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @param t       The throwable object
     * @see Level
     */
    public void log(Level level, String message, Throwable t) {
        this.log(level, message);
        t.printStackTrace();
    }

    /**
     * Disables this plugin.
     * The exception message is being logged to the Minecraft logfile and the stacktrace
     * is printed to STDOUT.
     *
     * @param e The exception that lead to disabling the plugin
     */
    public void disable(Exception e) {
        log(Level.SEVERE, String.format("Unrecoverable error: %s",
                (e.getMessage() == null)
                        ? "No message specified. Please specify a noticeable message when throwing this exception"
                        : e.getMessage()
        ));
        e.printStackTrace();
        log(Level.SEVERE, "Disabling plugin");
        plugin.getPluginLoader().disablePlugin(plugin);
    }

    /**
     * Disables this plugin.
     * The exception message is being logged to the Minecraft logfile and the stacktrace
     * is printed to STDOUT.
     * <p>
     * A given sender will be informed, that the plugin is being disabled.
     *
     * @param e      The exception that lead to disabling this plugin
     * @param sender The CommandSender that needs to be informed
     */
    @Override
    public void disable(Exception e, CommandSender sender) {
        sender.sendMessage(this.getMessagePrefix() + "Unrecoverable error. Disabling plugin");
        this.disable(e);
    }

    /**
     * Returns the plugin with the given name.
     * <p>
     *
     * @param name The plugin name
     * @return The corresponding plugin
     */
    @Override
    public BukkitPlugin getPlugin(String name) throws NoSuchPluginException {
        BukkitPlugin plugin = BukkitPlugin.wrap((JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(name));
        if (plugin == null) {
            throw new NoSuchPluginException(name);
        }
        return plugin;
    }

    /**
     * Returns the data folder of the plugin
     * @return The data folder File
     */
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
