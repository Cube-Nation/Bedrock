package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.config.ConfigService;

import java.io.File;
import java.util.logging.Level;

public interface FoundationPlugin {

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    void onPreEnable() throws Exception;

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    void onPostEnable() throws Exception;

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
     * Access to the ServiceManager instance and it's functions
     *
     * @return The ServiceManager instance
     */
    ServiceManager getServiceManager();

    /**
     * Returns the Bedrock ConfigService object instance.
     * If the ConfigService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ConfigService
     * @see ConfigService
     */
    ConfigService getConfigService();

    File getDataFolder();

    PluginDescription getPluginDescription();

}
