package de.cubenation.bedrock.service.configuration;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;

public class ConfigurationService extends AbstractService implements ServiceInterface {

    public ConfigurationService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {

    }

    @Override
    public void reload() throws ServiceReloadException {

    }
}
