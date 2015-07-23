package de.cubenation.bedrock.service.confirm;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.TimeoutException;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.HashMap;

public abstract class ConfirmService implements ConfirmInterface {

    private int timeout;

    private long created							    = (long) new Date().getTime();

    protected BukkitTask task;

    @SuppressWarnings("rawtypes")
    private HashMap<String, ConfirmStorable> storage	= new HashMap<>();

    /*
     * constructors
     */
    public ConfirmService() {
        this(BedrockPlugin.getInstance().getConfig().getInt("service.confirm.timeout"));
    }

    public ConfirmService(int timeout) {
        this.setTimeout(timeout);

        final ConfirmService csi = this;
        // create task with timeout
        // this task needs to be canceled when call() is executed via task.cancel();
        task = Bukkit.getServer().getScheduler().runTaskLater(BedrockPlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                csi.abort();
            }

        }, (long) this.getTimeout() * 20L);
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
