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

package de.cubenation.api.bedrock.service.confirm;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.TimeoutException;
import de.cubenation.api.bedrock.service.AbstractService;
import de.cubenation.plugin.bedrock.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public abstract class AbstractConfirmService extends AbstractService implements ConfirmInterface {

    private int timeout;

    private long created							    = (long) new Date().getTime();

    protected BukkitTask task;

    @SuppressWarnings("rawtypes")
    private HashMap<String, ConfirmStorable> storage;

    public AbstractConfirmService(BasePlugin plugin) {
        super(plugin);
        this.init((Integer) this.getConfigurationValue("service.confirm.timeout", 30));
    }

    public AbstractConfirmService(BasePlugin plugin, int timeout) {
        super(plugin);
        this.init(timeout);
    }

    private void init(int timeout) {
        this.storage = new HashMap<>();
        this.setTimeout(timeout);

        //this.getPlugin().log(Level.INFO, "  confirm service: New confirm service registered " + this.toString());

        final AbstractConfirmService csi = this;
        // create task with timeout
        // this task needs to be canceled when call() is executed via task.cancel();
        task = Bukkit.getServer().getScheduler().runTaskLater(BedrockPlugin.getInstance(), csi::abort, (long) this.getTimeout() * 20L);

    }

    @Override
    public void init() {
        this.init((Integer) this.getConfigurationValue("service.confirm.timeout", 30));
    }

    @Override
    final public void invalidate() {
        this.task.cancel();
    }

    @Override
    public void reload() {
        this.getPlugin().log(Level.WARNING, "  confirm service: Reloading of service confirm not supported");
    }

    /*
     * storage stuff
     */
    public void store(String key, ConfirmStorable<?> cs) {
        this.storage.put(key, cs);
    }

    public ConfirmStorable<?> get(String key) {
        return this.storage.get(key);
    }

    /*
     * timeout stuff
     */
    private void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @SuppressWarnings("WeakerAccess")
    public int getTimeout() {
        return this.timeout;
    }

    public void checkExceeded() throws TimeoutException {
        if (new Date().getTime() > this.created + ( this.timeout * 1000)) {
            this.abort();
            throw new TimeoutException();
        }
    }

    @Override
    public String toString() {
        return "(timeout: " + this.getTimeout() + ")";
    }

}
