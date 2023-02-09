package de.cubenation.bedrock.core.service.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.database.Database;
import de.cubenation.bedrock.core.database.HibernateOrmDatabase;
import de.cubenation.bedrock.core.exception.DatastoreInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;

public class DatabaseService extends AbstractService {

    /**
     * Path to directory containing database drivers in the plugins directory.
     */
    private static final String DRIVER_PATH = "lib";
    private final Path driverDirectory = Paths.get(plugin.getFallbackBedrockPlugin().getPluginFolder().getAbsolutePath(), DRIVER_PATH);

    private final HashMap<String, Database> databases = new HashMap<>();

    public DatabaseService(FoundationPlugin plugin) {
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

        for (de.cubenation.bedrock.core.annotation.Database annotation : plugin.getClass().getAnnotationsByType(de.cubenation.bedrock.core.annotation.Database.class)) {
            String id = annotation.name();
            if (id == null || StringUtils.isBlank(id)) {
                // TODO: Sanitize
                throw new ServiceInitException(new DatastoreInitException("Database identifier cannot be empty or null"));
            }

            int entityCount = annotation.entities() != null ? annotation.entities().length : 0;
            plugin.log(Level.INFO, String.format("Initializing database '%s' with %s entities...", annotation.name(), entityCount));

            Database database = new HibernateOrmDatabase(plugin, id);
            try {
                database.init(annotation.entities());
            } catch (DatastoreInitException e) {
                throw new ServiceInitException(e);
            }

            databases.put(id, database);
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

    public Session openSession(String dbIdentifier) {
        String id = dbIdentifier.toLowerCase();
        if (!databases.containsKey(id)) {
            throw new IllegalArgumentException(id + " is not a valid database identifier");
        }
        return databases.get(id).openSession();
    }

    public boolean loadDrivers() throws ServiceInitException {
        if(!Files.isDirectory(driverDirectory)){
            plugin.log(Level.SEVERE, "Could not load database driver since the specified directory '" + DRIVER_PATH + "' does not exist inside the plugin's directory.");
            return false;
        }


        try {
            Files.list(driverDirectory).forEach(path -> {
                try (URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()}, getClass().getClassLoader())) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException | IOException e) {
                    plugin.log(Level.SEVERE, "Could not load database driver: " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new ServiceInitException(e);
        }

        return true;
    }

    private void clear() {
        databases.values().forEach(database -> {
            try {
                database.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
