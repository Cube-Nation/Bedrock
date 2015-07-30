package de.cubenation.bedrock.service.pluginconfig;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PluginConfigService extends AbstractService implements ServiceInterface {

    private boolean has_config  = true;

    public PluginConfigService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        try {
            this.check();
        } catch (IOException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }

        if (this.has_config)
            this.getPlugin().reloadConfig();
    }

    private void check() throws IOException {
        // check if plugin data folder exists and create if not
        if (!this.plugin.getDataFolder().exists() && !this.plugin.getDataFolder().mkdirs())
            throw new IOException("Could not create folder " + this.plugin.getDataFolder().getName());

        File file = new File(this.plugin.getDataFolder(), "config.yml");
        if (!file.exists())
            this.create();
    }

    private void create() {
        this.plugin.log(Level.INFO, "Creating config.yml!");
        try {
            this.plugin.saveDefaultConfig();
        } catch (Exception e) {
            this.has_config = false;
            this.plugin.log(Level.WARNING, "This plugin does not contain a configuration file. All settings are taken from the Bedrock plugin");
        }

        this.has_config = true;
    }

    public FileConfiguration getConfig() {
        return (!this.has_config) ? null : this.plugin.getConfig();
    }

}