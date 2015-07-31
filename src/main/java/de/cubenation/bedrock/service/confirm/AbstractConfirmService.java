package de.cubenation.bedrock.service.confirm;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.TimeoutException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings("unused")
public abstract class AbstractConfirmService extends AbstractService implements ServiceInterface, ConfirmInterface {

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

        this.getPlugin().log(Level.INFO, "  confirm service: New confirm service registered " + this.toString());

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
        this.init((Integer) this.getConfigurationValue("service.confirm.timeout", 30));
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
