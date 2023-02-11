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

package de.cubenation.bedrock.bukkit.api.service.confirm;

import de.cubenation.bedrock.bukkit.api.BasePlugin;
import de.cubenation.bedrock.core.exception.TimeoutException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.bukkit.plugin.BedrockPlugin;
import de.cubenation.bedrock.core.service.confirm.ConfirmInterface;
import de.cubenation.bedrock.core.service.confirm.ConfirmStorable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public abstract class AbstractConfirmService extends AbstractService implements ConfirmInterface {

    @Getter @Setter
    private int timeout;

    private final long created = new Date().getTime();

    protected BukkitTask task;

    private HashMap<String, ConfirmStorable<?>> storage;

    public AbstractConfirmService(BasePlugin plugin) {
        super(plugin);
        init((Integer) getConfigurationValue("service.confirm.timeout", 30));
    }

    public AbstractConfirmService(BasePlugin plugin, int timeout) {
        super(plugin);
        init(timeout);
    }

    private void init(int timeout) {
        storage = new HashMap<>();
        setTimeout(timeout);

        //this.getPlugin().log(Level.INFO, "  confirm service: New confirm service registered " + this.toString());

        final AbstractConfirmService csi = this;
        // create task with timeout
        // this task needs to be canceled when call() is executed via task.cancel();
        task = Bukkit.getServer().getScheduler().runTaskLater(BedrockPlugin.getInstance(), csi::abort, (long) getTimeout() * 20L);

    }

    @Override
    public void init() {
        init((Integer) getConfigurationValue("service.confirm.timeout", 30));
    }

    @Override
    final public void invalidate() {
        task.cancel();
    }

    @Override
    public void reload() {
        plugin.log(Level.WARNING, "  confirm service: Reloading of service confirm not supported");
    }

    /*
     * storage stuff
     */
    public void store(String key, ConfirmStorable<?> cs) {
        storage.put(key, cs);
    }

    public ConfirmStorable<?> get(String key) {
        return storage.get(key);
    }

    /*
     * timeout stuff
     */
    public void checkExceeded() throws TimeoutException {
        if (new Date().getTime() > created + ( timeout * 1000L)) {
            abort();
            throw new TimeoutException();
        }
    }

    @Override
    public String toString() {
        return "(timeout: " + getTimeout() + ")";
    }

}
