package de.cubenation.bedrock.service.confirm;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.TimeoutException;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.HashMap;

public abstract class AbstractConfirmService implements ServiceInterface, ConfirmInterface {

    private int timeout;

    private long created							    = (long) new Date().getTime();

    protected BukkitTask task;

    @SuppressWarnings("rawtypes")
    private HashMap<String, ConfirmStorable> storage;

    /*
     * constructors
     */
    public AbstractConfirmService() {
        this(BedrockPlugin.getInstance().getPluginConfigService().getConfig().getInt("service.confirm.timeout"));
    }

    public AbstractConfirmService(int timeout) {
        this.setTimeout(timeout);

        final AbstractConfirmService csi = this;
        // create task with timeout
        // this task needs to be canceled when call() is executed via task.cancel();
        task = Bukkit.getServer().getScheduler().runTaskLater(BedrockPlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                csi.abort();
            }

        }, (long) this.getTimeout() * 20L);
    }


    @Override
    public void init() {
        this.storage = new HashMap<>();
    }

    @Override
    public void reload() {
        this.init();
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

    public int getTimeout() {
        return this.timeout;
    }

    public void checkExceeded() throws TimeoutException {
        if (new Date().getTime() > this.created + ( this.timeout * 1000)) {
            this.abort();
            throw new TimeoutException();
        }
    }

}
