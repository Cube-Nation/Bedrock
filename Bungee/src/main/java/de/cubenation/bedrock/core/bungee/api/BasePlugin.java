package de.cubenation.bedrock.core.bungee.api;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.config.ConfigService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BasePlugin extends Plugin implements FoundationPlugin {

    /**
     * The ServiceManager object
     */
    private ServiceManager serviceManager;

    /**
     * BasePlugin constructor
     * Calls the Plugin constructor
     *
     * @see Plugin
     */
    public BasePlugin() {
        super();
    }

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    public void onPreEnable() throws Exception {
    }

    @Override
    public void onEnable() {
        try {
            this.onPreEnable();
        } catch (Exception e) {
            this.disable(e);
            return;
        }

        // initialize ServiceManager
        this.serviceManager = new ServiceManager(this);
        try {
            this.serviceManager.registerServices();
        } catch (ServiceInitException e) {
            this.log(Level.SEVERE, "Loading services failed");
            this.disable(e);
        }

        // call onPostEnable after we've set everything up
        try {
            this.onPostEnable();
        } catch (Exception e) {
            this.disable(e);
        }
    }

    /**
     * Throw an Exception to disable the plugin.
     *
     * @throws Exception if any error occurs.
     */
    public void onPostEnable() throws Exception {
    }


    /**
     * Disables this plugin.
     * The exception message is being logged to the Minecraft logfile and the stacktrace
     * is printed to STDOUT.
     *
     * @param e The exception that lead to disabling the plugin
     */
    @SuppressWarnings("WeakerAccess")
    public void disable(Exception e) {
        log(Level.SEVERE, String.format("Unrecoverable error: %s",
                (e.getMessage() == null)
                        ? "No message specified. Please specify a noticeable message when throwing this exception"
                        : e.getMessage()
        ));
        e.printStackTrace();
        log(Level.SEVERE, "Disabling plugin");

        // TODO: tbd
    }

    /**
     * Log a message with a given log level to the Minecraft logfile
     *
     * @param level   The Log4J log level
     * @param message The message to log
     * @see Level
     */
    public void log(Level level, String message) {
        // TODO: tbd
        Logger.getLogger("Minecraft").log(
                level,
                ChatColor.stripColor(String.format("%s %s", "BedrockPlugin TDB"/*this.getMessagePrefix()*/, message))
        );
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
     * Access to the ServiceManager instance and it's functions
     *
     * @return The ServiceManager instance
     */
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    /**
     * Returns the Bedrock ConfigService object instance.
     * If the ConfigService is not ready, <code>null</code> is returned.
     *
     * @return The Bedrock ConfigService
     * @see ConfigService
     */
    public ConfigService getConfigService() {
        return (ConfigService) this.getServiceManager().getService(ConfigService.class);
    }

}
