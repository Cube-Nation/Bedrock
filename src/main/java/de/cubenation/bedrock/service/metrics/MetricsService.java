package de.cubenation.bedrock.service.metrics;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.logging.Level;

public class MetricsService extends AbstractService implements ServiceInterface{

    public MetricsService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        if (!this.getPlugin().getPluginConfigService().getConfig().getBoolean("service.metrics.use")) {
            this.getPlugin().log(Level.WARNING, "Disabling metrics");
            return;
        }

        try {
            Metrics metrics = new Metrics(this.getPlugin());
            metrics.start();
        } catch (IOException e) {
            this.getPlugin().log(Level.WARNING, "Failed to submit metrics");
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.getPlugin().log(Level.WARNING,
                "Reloading the metrics service is not supported. Please restart the server if you modified the metrics service"
        );
    }
}
