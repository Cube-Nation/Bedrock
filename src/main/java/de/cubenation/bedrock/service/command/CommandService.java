package de.cubenation.bedrock.service.command;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;

public class CommandService extends AbstractService implements ServiceInterface {

    public CommandService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {

    }

    @Override
    public void reload() throws ServiceReloadException {

    }


}
