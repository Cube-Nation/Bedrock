package de.cubenation.bedrock.core.service;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Service;
import de.cubenation.bedrock.core.exception.ServiceAlreadyExistsException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownServiceException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class ServiceManager {

    private FoundationPlugin plugin;

    private boolean intentionally_ready = false;

    // A LinkedHashMap preserves the order of its elements which is elementary for all services
    private LinkedHashMap<Class<? extends AbstractService>, AbstractService> services = new LinkedHashMap<>();

    public ServiceManager(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    protected FoundationPlugin getPlugin() {
        return this.plugin;
    }

    public void registerServices() throws ServiceInitException {
        try {

            // find and register custom Service annotations
            for (Service service : this.getPlugin().getClass().getAnnotationsByType(Service.class)) {
                this.getPlugin().log(Level.INFO, "Registering custom service " + service.value());
                this.registerService(service.value());
            }

        } catch (ServiceAlreadyExistsException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    public void registerService(Class<? extends AbstractService> clazz) throws ServiceAlreadyExistsException, ServiceInitException {
        if (this.exists(clazz))
            throw new ServiceAlreadyExistsException(clazz.toString());

        AbstractService service;
        try {
            Constructor<? extends AbstractService> cosntructor = clazz.getConstructor(FoundationPlugin.class);
            service = cosntructor.newInstance(this.plugin);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInitException(e.getMessage());
        }

        service.init();
        this.services.put(clazz, service);
    }

    public boolean exists(Class<? extends AbstractService> clazz) {
        return this.services.containsKey(clazz);
    }

    public AbstractService getService(Class<? extends AbstractService> clazz) {
        if (!this.exists(clazz) && this.intentionally_ready) {
            this.getPlugin().log(Level.SEVERE, "Could not retrieve service: " + clazz.toString() + ":");
            Arrays.stream(Thread.currentThread().getStackTrace()).forEach(
                    stackTraceElement -> this.getPlugin().log(Level.SEVERE, stackTraceElement.toString())
            );
        }

        return this.services.getOrDefault(clazz, null);
    }

    public void reload(Class<? extends AbstractService> clazz) throws UnknownServiceException, ServiceReloadException {
        if (!this.exists(clazz)) throw new UnknownServiceException(clazz.toString());
        this.getService(clazz).reload();
    }

    public void reload() throws ServiceReloadException {
        for (Class<? extends AbstractService> clazz : this.services.keySet()) {
            this.getService(clazz).reload();
        }
    }

    public void setIntentionallyReady(boolean intentionally_ready) {
        this.intentionally_ready = intentionally_ready;
    }
}

