package de.cubenation.bedrock.service;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.exception.UnknownServiceException;

import java.util.HashMap;
import java.util.logging.Level;

public class ServiceManager {

    private BasePlugin plugin;

    private HashMap<String,ServiceInterface> services = new HashMap<>();

    public ServiceManager(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public ServiceInterface registerService(String name, ServiceInterface service) throws ServiceInitException {
        this.plugin.log(Level.INFO, "Initializing Service " + name);

        this.services.put(name, service);
        this.services.get(name).init();

        return this.services.get(name);
    }

    public ServiceInterface getService(String name) throws UnknownServiceException {
        if (!this.exists(name)) throw new UnknownServiceException(name);
        return this.services.get(name);
    }

    public boolean exists(String name) {
        return this.services.containsKey(name);
    }

    public void reload(String name) throws ServiceReloadException {
        try {
            this.getService(name).reload();
        } catch (UnknownServiceException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

}
