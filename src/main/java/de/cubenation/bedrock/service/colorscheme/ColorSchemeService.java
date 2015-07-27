package de.cubenation.bedrock.service.colorscheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;

public class ColorSchemeService implements ServiceInterface {

    private BasePlugin plugin;

    public ColorSchemeService(BasePlugin plugin) {
        this.setPlugin(plugin);
    }

    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws ServiceInitException {
        this.getPlugin().setColorScheme(null);
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

}
