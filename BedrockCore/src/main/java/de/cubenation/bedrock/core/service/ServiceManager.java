/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.service;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Service;
import de.cubenation.bedrock.core.exception.InjectionException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.injection.InstanceInjector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Manages available instances of {@link AbstractService}
 */
public class ServiceManager {

    private final FoundationPlugin plugin;

    private boolean intentionallyReady = false;

    // A LinkedHashMap preserves the order of its elements which is elementary for all services
    private final LinkedHashMap<Class<? extends AbstractService>, AbstractService> services = new LinkedHashMap<>();

    public ServiceManager(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register a service for each {@link Service} annotation on the plugin class.
     * @throws ServiceInitException in case at least one registration fails
     */
    public void registerCustomServices() throws ServiceInitException {
        // Find and register custom Service annotations
        for (Service service : plugin.getClass().getAnnotationsByType(Service.class)) {
            plugin.log(Level.INFO, "Registering custom service " + service.value());
            registerService(service.value());
        }
    }

    /**
     * Register an implementation of the {@link AbstractService} class.
     * @param clazz class to register
     * @throws ServiceInitException in case the registration fails
     * @return the created service instance
     */
    @SuppressWarnings("UnusedReturnValue")
    public AbstractService registerAndInitializeService(Class<? extends AbstractService> clazz) throws ServiceInitException {
        AbstractService service = registerService(clazz);
        initService(service);
        return service;
    }

    /**
     * Register an implementation of the {@link AbstractService} class.
     * @param clazz class to register
     * @throws ServiceInitException in case the registration fails
     * @return the created service instance
     */
    public AbstractService registerService(Class<? extends AbstractService> clazz) throws ServiceInitException {
        if (this.exists(clazz)) {
            throw new ServiceInitException(String.format("A service of the class %s is already registered", clazz.toString()));
        }

        AbstractService service;
        try {
            Constructor<? extends AbstractService> constructor = clazz.getConstructor(FoundationPlugin.class);
            service = constructor.newInstance(plugin);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInitException(e);
        }

        this.services.put(clazz, service);
        return service;
    }

    /**
     * Register an implementation of the {@link AbstractService} class under another class name.
     * @param clazz class to register
     * @throws ServiceInitException in case the registration fails
     * @return the created service instance
     */
    @SuppressWarnings("UnusedReturnValue")
    public AbstractService registerAndInitializeService(Class<? extends AbstractService> target, Class<? extends AbstractService> clazz) throws ServiceInitException {
        AbstractService service = registerService(target, clazz);
        initService(service);
        return service;
    }

    /**
     * Register an implementation of the {@link AbstractService} class under another class name.
     * @param clazz class to register
     * @throws ServiceInitException in case the registration fails
     * @return the created service instance
     */
    public AbstractService registerService(Class<? extends AbstractService> target, Class<? extends AbstractService> clazz) throws ServiceInitException {
        if (this.exists(clazz)) {
            throw new ServiceInitException(String.format("A service of the class %s is already registered", clazz.toString()));
        }

        AbstractService service;
        try {
            Constructor<? extends AbstractService> constructor = clazz.getConstructor(FoundationPlugin.class);
            service = constructor.newInstance(plugin);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInitException(e);
        }

        this.services.put(target, service);
        return service;
    }

    /**
     * Initialize all registered services which are not initialized yet.
     * @throws ServiceInitException in case at least one initialization fails
     */
    public void initServices() throws ServiceInitException {
        Collection<AbstractService> remainingServices = services.values().stream().filter(Predicate.not(AbstractService::isInitialized)).toList();
        for (AbstractService service : remainingServices) {
            initService(service);
            plugin.log(Level.WARNING, "Init "+service.getClass().getName());
        }
    }

    /**
     * Initialize a service.
     * @throws ServiceInitException in case at least one initialization fails
     */
    private void initService(AbstractService service) throws ServiceInitException {
        try {
            InstanceInjector.performInjection(plugin, service);
        } catch (InjectionException e) {
            throw new ServiceInitException(e);
        }
        service.init();
        service.setInitialized(true);
    }

    /**
     * Checks whether a class is already registered as a {@link AbstractService}.
     * @param clazz class to check
     * @return true if the class is already registered
     */
    public boolean exists(Class<? extends AbstractService> clazz) {
        return this.services.containsKey(clazz);
    }

    /**
     * Get the instance of a registered {@link AbstractService}.
     * @param clazz class of the {@link AbstractService}
     * @return instance of {@link AbstractService} or {@literal null} if not existent
     */
    public AbstractService getService(Class<? extends AbstractService> clazz) {
        if (!this.exists(clazz) && intentionallyReady) {
            plugin.log(Level.SEVERE, "Could not retrieve service: " + clazz.toString() + ":");
            Arrays.stream(Thread.currentThread().getStackTrace()).forEach(
                    stackTraceElement -> plugin.log(Level.SEVERE, stackTraceElement.toString())
            );
        }

        return services.getOrDefault(clazz, null);
    }

    /**
     * Reload a specific {@link AbstractService}.
     * @param clazz class of the {@link AbstractService}.
     * @throws ServiceReloadException in case at least one reload fails
     */
    public void reload(Class<? extends AbstractService> clazz) throws ServiceReloadException {
        if (!this.exists(clazz)) throw new IllegalArgumentException(String.format("%s is no valid service or was never registered", clazz.toString()));
        this.getService(clazz).reload();
    }

    /**
     * Reload all services.
     * @throws ServiceReloadException in case the reload fails
     */
    public void reload() throws ServiceReloadException {
        for (Class<? extends AbstractService> clazz : this.services.keySet()) {
            this.getService(clazz).reload();
        }
    }

    /**
     * Set whether all base services were initialized.
     * @param intentionallyReady value
     */
    public void setIntentionallyReady(boolean intentionallyReady) {
        this.intentionallyReady = intentionallyReady;
    }
}

