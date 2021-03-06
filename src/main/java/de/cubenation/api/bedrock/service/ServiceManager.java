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

package de.cubenation.api.bedrock.service;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Service;
import de.cubenation.api.bedrock.exception.ServiceAlreadyExistsException;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.exception.UnknownServiceException;
import de.cubenation.api.bedrock.service.colorscheme.ColorSchemeService;
import de.cubenation.api.bedrock.service.command.CommandService;
import de.cubenation.api.bedrock.service.config.ConfigService;
import de.cubenation.api.bedrock.service.inventory.InventoryService;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import de.cubenation.api.bedrock.service.settings.SettingsService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class ServiceManager {

    private BasePlugin plugin;

    private boolean intentionally_ready = false;

    // A LinkedHashMap preserves the order of its elements which is elementary for all services
    private LinkedHashMap<Class<? extends AbstractService>, AbstractService> services = new LinkedHashMap<>();

    public ServiceManager(BasePlugin plugin) {
        this.plugin = plugin;
    }

    protected BasePlugin getPlugin() {
        return this.plugin;
    }

    public void registerServices() throws ServiceInitException {
        try {
            // DO NOT MODIFY THIS ORDER!
            this.registerService(ConfigService.class);
            this.registerService(ColorSchemeService.class);
            this.intentionally_ready = true;
            this.registerService(LocalizationService.class);
            this.registerService(SettingsService.class);
            this.registerService(CommandService.class);
            this.registerService(PermissionService.class);
            this.registerService(InventoryService.class);

            // find and register custom Service annotations
            for (Service service : this.getPlugin().getClass().getAnnotationsByType(Service.class)) {
                this.getPlugin().log(Level.INFO, "Registering custom service " + service.value());
                this.registerService(service.value());
            }

        } catch (ServiceAlreadyExistsException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    private void registerService(Class<? extends AbstractService> clazz) throws ServiceAlreadyExistsException, ServiceInitException {
        if (this.exists(clazz))
            throw new ServiceAlreadyExistsException(clazz.toString());

        AbstractService service;
        try {
            Constructor<? extends AbstractService> cosntructor = clazz.getConstructor(BasePlugin.class);
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

}
