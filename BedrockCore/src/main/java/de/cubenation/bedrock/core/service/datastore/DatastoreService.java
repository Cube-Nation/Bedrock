package de.cubenation.bedrock.core.service.datastore;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.DatastoreConfig;
import de.cubenation.bedrock.core.datastore.Datastore;
import de.cubenation.bedrock.core.datastore.DatastoreSession;
import de.cubenation.bedrock.core.datastore.HibernateOrmDatastore;
import de.cubenation.bedrock.core.exception.DatastoreInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

public class DatastoreService extends AbstractService {

    /**
     * Path to directory containing datasource drivers in the plugins directory.
     */
    private static final String DRIVER_PATH = "config/drivers";
    private final Path driverDirectory = Paths.get(plugin.getFallbackBedrockPlugin().getDataFolder().getAbsolutePath(), DRIVER_PATH);

    private final HashMap<String, Datastore> datastores = new HashMap<>();

    public DatastoreService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        clear();

        try {
            Files.createDirectories(driverDirectory);
        } catch (IOException e) {
            throw new ServiceInitException("Could not create driver directory", e);
        }

        // TODO: loadDrivers();

        // TODO: DatastoreConfig datastoreConfig = (DatastoreConfig) this.plugin.getConfigService().getConfig(DatastoreConfig.class);

        for (de.cubenation.bedrock.core.annotation.Datastore annotation : plugin.getClass().getAnnotationsByType(de.cubenation.bedrock.core.annotation.Datastore.class)) {
            String id = annotation.name();
            if (id == null || annotation.name().trim().length() == 0) {
                // TODO: Sanitize
                throw new ServiceInitException(new DatastoreInitException("Datastore identifier cannot be empty or null"));
            }

            int entityCount = annotation.entities() != null ? annotation.entities().length : 0;
            plugin.log(Level.INFO, String.format("Initializing datastore '%s' with %s entities...", annotation.name(), entityCount));

            Datastore datastore = new HibernateOrmDatastore(plugin, id);
            try {
                datastore.init(annotation.entities());
            } catch (DatastoreInitException e) {
                throw new ServiceInitException(e);
            }

            datastores.put(id, datastore);
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage(), e.getCause());
        }
    }

    public DatastoreSession openSession(String dbIdentifier) {
        String id = dbIdentifier.toLowerCase();
        if (!datastores.containsKey(id)) {
            throw new IllegalArgumentException(id + " is not a valid datastore identifier");
        }
        return datastores.get(id).openSession();
    }

    public boolean loadDrivers() throws ServiceInitException {
        if(!Files.isDirectory(driverDirectory)){
            plugin.log(Level.SEVERE, "Could not load data source connectors since the specified directory '" + DRIVER_PATH + "' does not exist inside the plugin's directory.");
            return false;
        }


        try {
            Files.list(driverDirectory).forEach(path -> {
                try (URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()}, getClass().getClassLoader())) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException | IOException e) {
                    plugin.log(Level.SEVERE, "Could not load data source connector: " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new ServiceInitException(e);
        }

        return true;
    }

    private void clear() {
        datastores.values().forEach(datastore -> {
            try {
                datastore.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
