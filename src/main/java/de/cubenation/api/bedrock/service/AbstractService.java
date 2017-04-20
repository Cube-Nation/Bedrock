package de.cubenation.api.bedrock.service;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.plugin.bedrock.BedrockPlugin;

import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class AbstractService {

    protected BasePlugin plugin;

    public AbstractService(BasePlugin plugin) {
        this.setPlugin(plugin);
    }

    protected void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    protected BasePlugin getPlugin() {
        return this.plugin;
    }

    protected Object getConfigurationValue(String path, Object def) {
        Object value;

        this.plugin.log(Level.FINER, "Retrieving configuration path " + path + " from plugin configuration");

        try {
            value = this.plugin.getConfigService().getReadOnlyConfig().get(path);
            if (value == null || value.toString().isEmpty())
                throw new NullPointerException();

            return value;
        } catch (NullPointerException e) {
            this.plugin.log(Level.FINER, "Could not fetch path from plugin configuration");
        }

        try {
            value = BedrockPlugin.getInstance().getConfigService().getReadOnlyConfig().get(path);
            if (value == null || value.toString().isEmpty())
                throw new NullPointerException();

            return value;
        } catch (NullPointerException e) {
            this.plugin.log(Level.FINER, "Could not fetch path from Bedrock plugin configuration");
        }

        return def;
    }

    public abstract void init() throws ServiceInitException;

    public abstract void reload() throws ServiceReloadException;

}