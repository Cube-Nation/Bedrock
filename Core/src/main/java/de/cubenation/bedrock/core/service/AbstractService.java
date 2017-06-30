package de.cubenation.bedrock.core.service;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;

import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class AbstractService {

    protected FoundationPlugin plugin;

    public AbstractService(FoundationPlugin plugin) {
        this.setPlugin(plugin);
    }

    protected void setPlugin(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    protected FoundationPlugin getPlugin() {
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
