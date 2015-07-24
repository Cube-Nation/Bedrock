package de.cubenation.bedrock.service;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;

import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;

public class ServiceManager {

    private BasePlugin plugin;

    private HashMap<String,ServiceInterface> services = new HashMap<>();

    public ServiceManager(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public void registerService(String name, ServiceInterface service) throws ServiceInitException {
        this.services.put(name, service);
        this.services.get(name).init();
    }

    public ServiceInterface getService(String name) throws UnknownServiceException {
        if (!this.exists(name)) throw new UnknownServiceException(name);
        return this.services.get(name);
    }

    public boolean exists(String name) {
        return this.services.containsKey(name);
    }

    public void reloadAll() throws ServiceReloadException {
        for (Map.Entry entry : this.services.entrySet()) {
            ServiceInterface service = (ServiceInterface) entry.getValue();
            service.reload();
        }
    }

    public void reload(String name) throws ServiceReloadException {
        try {
            this.getService(name).reload();
        } catch (UnknownServiceException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

}
