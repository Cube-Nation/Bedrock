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
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import lombok.Getter;
import lombok.Setter;

import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class AbstractService {

    protected FoundationPlugin plugin;

    /**
     * If true, the service was successfully initialized.
     * Has to be set manually, if service is not registered via {@link ServiceManager#registerService(Class)}.
     */
    @Getter
    @Setter
    protected boolean initialized = false;

    public AbstractService(FoundationPlugin plugin) {
        this.setPlugin(plugin);
    }

    protected void setPlugin(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    protected FoundationPlugin getPlugin() {
        return this.plugin;
    }

    protected Object getConfigurationValue(String path, Object def) {
        Object value;

        this.plugin.log(Level.FINER, "Retrieving configuration path " + path + " from plugin configuration");

        try {
            value = this.plugin.getConfigService().getReadOnlyConfig().get(path);
            if (value == null || value.toString().isEmpty())
                throw new NullPointerException();

            return value;
        } catch (NullPointerException e) {
            this.plugin.log(Level.FINER, "Could not fetch path from plugin configuration");
        }

        // Don't do this for Bedrock Plugin Instance
        if (!plugin.isFallbackBedrockPlugin()) {
            try {
                value = plugin.getFallbackBedrockPlugin().getConfigService().getReadOnlyConfig().get(path);
                if (value == null || value.toString().isEmpty())
                    throw new NullPointerException();

                return value;
            } catch (NullPointerException e) {
                this.plugin.log(Level.FINER, "Could not fetch path from Bedrock plugin configuration");
            }
        }

        return def;
    }

    public abstract void init() throws ServiceInitException;

    public abstract void reload() throws ServiceReloadException;

}
