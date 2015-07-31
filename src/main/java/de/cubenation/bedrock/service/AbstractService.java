package de.cubenation.bedrock.service;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;

import java.util.logging.Level;

public class AbstractService {

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
            value = this.plugin.getConfigService().getConfig().get(path);
            if (value == null || value.toString().isEmpty())
                throw new NullPointerException();

            return value;
        } catch (NullPointerException e) {
            this.plugin.log(Level.FINER, "Could not fetch path from plugin configuration");
        }

        try {
            value = BedrockPlugin.getInstance().getConfigService().getConfig().get(path);
            if (value == null || value.toString().isEmpty())
                throw new NullPointerException();

            return value;
        } catch (NullPointerException e) {
            this.plugin.log(Level.FINER, "Could not fetch path from Bedrock plugin configuration");
        }

        return def;
    }
}
