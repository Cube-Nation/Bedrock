package de.cubenation.bedrock.service.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;

public class CommandService implements ServiceInterface {

    private BasePlugin plugin;

    public CommandService(BasePlugin plugin) {
        this.setPlugin(plugin);
    }

    private BasePlugin getPlugin() {
        return plugin;
    }

    private void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws ServiceInitException {

    }

    @Override
    public void reload() throws ServiceReloadException {

    }


}
