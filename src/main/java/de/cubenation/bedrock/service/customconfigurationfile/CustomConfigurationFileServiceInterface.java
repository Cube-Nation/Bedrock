package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;


public class CustomConfigurationFileServiceInterface implements ServiceInterface {

    private BasePlugin plugin;

    public CustomConfigurationFileServiceInterface(BasePlugin plugin) {
        this.setPlugin(plugin);
    }

    @Override
    public void init() throws ServiceInitException {

    }

    @Override
    public void reload() throws ServiceReloadException {

    }

    /*
     * Plugin Getter/Setter
     */
    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}
