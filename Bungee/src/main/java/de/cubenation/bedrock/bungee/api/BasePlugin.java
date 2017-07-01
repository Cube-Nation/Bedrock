package de.cubenation.bedrock.bungee.api;

import de.cubenation.bedrock.bungee.api.service.config.BungeeConfigService;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.exception.ServiceAlreadyExistsException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.plugin.PluginDescription;
import de.cubenation.bedrock.core.service.ServiceManager;
import de.cubenation.bedrock.core.service.colorscheme.ColorSchemeService;
import de.cubenation.bedrock.core.service.settings.SettingsService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.logging.Level;

public class BasePlugin extends DatabasePlugin implements FoundationPlugin {

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
            // TODO: tbd
            // DO NOT MODIFY THIS ORDER!
            serviceManager.registerService(BungeeConfigService.class);
            serviceManager.registerService(ColorSchemeService.class);
            serviceManager.setIntentionallyReady(true);
//            this.registerService(LocalizationService.class);
            serviceManager.registerService(SettingsService.class);
//            this.registerService(CommandService.class);
//            this.registerService(PermissionService.class);
//            this.registerService(InventoryService.class);

            this.serviceManager.registerServices();
        } catch (ServiceInitException | ServiceAlreadyExistsException e) {
            this.log(Level.SEVERE, "Loading services failed");
            this.disable(e);
        }

        try {
            BedrockDefaults bedrockDefaults = (BedrockDefaults) getConfigService().getConfig(BedrockDefaults.class);
            setupDatabase(bedrockDefaults.getDatabaseConfiguration());
        } catch (Exception e) {
            this.disable(e);
            return;
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

    @Override
    public Boolean isDatabaseEnabled() {
        return true;
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

        // TODO: tbd disable
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(
                level,
                ChatColor.stripColor(String.format("%s %s", "BedrockPlugin TDB"/*this.getMessagePrefix()*/, message))
        );
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        this.log(level, message);
        t.printStackTrace();
    }


    @Override
    public ArrayList<Class<?>> getCustomSettingsFiles() {
        return null;
    }

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public BungeeConfigService getConfigService() {
        return (BungeeConfigService) this.getServiceManager().getService(BungeeConfigService.class);
    }

    @Override
    public SettingsService getSettingService() {
        return (SettingsService) this.getServiceManager().getService(SettingsService.class);
    }

    @Override
    public PluginDescription getPluginDescription() {
        return new PluginDescription(getDescription().getName(),
                getDescription().getMain(),
                getDescription().getVersion(),
                getDescription().getAuthor(),
                getDescription().getDepends(),
                getDescription().getSoftDepends(),
                getDescription().getFile(),
                getDescription().getDescription());
    }


}
