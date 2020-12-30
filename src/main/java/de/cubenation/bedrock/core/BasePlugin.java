package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.exception.NoSuchPluginException;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a generic plugin.
 *
 * @author Cube-Nation
 * @version 2.0
 */
public interface BasePlugin {

    /**
     * Returns the plugin name as a string.
     * <p>
     *
     * @return The plugin name
     */
    String getPrettyName();

    /**
     * Returns the plugin version as a string.
     * <p>
     *
     * @return The plugin version
     */
    String getPluginVersion();

    /**
     * Returns a string of the current plugin, known as the message prefix.
     * <p>
     *
     * @return The message prefix
     */
    String getMessagePrefix();

    /**
     * Returns a string of a given plugin, known as the message prefix.
     * <p>
     *
     * @param plugin The plugin
     * @return The message prefix
     */
    <P extends BasePlugin> String getMessagePrefix(P plugin);

    /**
     * Returns a string with the plugin name, known as the message prefix.
     * <p>
     *
     * @param plugin The plugin name
     * @return The message prefix
     */
    String getMessagePrefix(String plugin);

    /**
     * Log a message with a given log level to the Minecraft logfile
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @see Level
     */
    void log(Level level, String message);

    /**
     * Log a message with a given log level to the Minecraft logfile.
     * The stacktrace of the Throwable object is printed to STDOUT.
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @param t       The throwable object
     * @see Level
     */
    void log(Level level, String message, Throwable t);

    /**
     * Returns the Logger of the plugin
     * @return The plugin Logger
     */
    Logger getLogger();

    /**
     * Disables this plugin.
     * The exception message is being logged to the Minecraft logfile and the stacktrace
     * is printed to STDOUT.
     *
     * @param e The exception that lead to disabling the plugin
     */
    void disable(Exception e);

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
    @SuppressWarnings("unused")
    void disable(Exception e, CommandSender sender);

    /**
     * Returns a plugin object
     *
     * @param name The plugin name
     * @return The requested plugin object
     * @throws NoSuchPluginException if a Plugin is missing.
     */
    BasePlugin getPlugin(String name) throws NoSuchPluginException;

    /**
     * Returns the data folder of the plugin
     * @return The data folder File
     */
    File getDataFolder();
}
