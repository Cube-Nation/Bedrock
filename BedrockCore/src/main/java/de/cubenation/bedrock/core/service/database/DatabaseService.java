package de.cubenation.bedrock.core.service.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.config.DatabaseConfig;
import de.cubenation.bedrock.core.database.Database;
import de.cubenation.bedrock.core.database.HibernateOrmDatabase;
import de.cubenation.bedrock.core.exception.StorageInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class DatabaseService extends AbstractService {

    @Inject
    private ConfigService configService;

    private final HashMap<String, Database> databases = new HashMap<>();

    public DatabaseService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        // Load config
        try {
            configService.registerClass(DatabaseConfig.class);
        } catch (InstantiationException e) {
            throw new ServiceInitException(e.getMessage());
        }

        // TODO: Automatically load specified drivers

        // Create database connections
        for (de.cubenation.bedrock.core.annotation.Database annotation : plugin.getClass().getAnnotationsByType(de.cubenation.bedrock.core.annotation.Database.class)) {
            String id = annotation.name();
            if (id == null || StringUtils.isBlank(id)) {
                // TODO: Sanitize
                throw new ServiceInitException(new StorageInitException("Database identifier cannot be empty or null"));
            }

            int entityCount = annotation.entities() != null ? annotation.entities().length : 0;
            plugin.log(Level.INFO, String.format("Initializing database '%s' with %s entities...", annotation.name(), entityCount));

            Database database = new HibernateOrmDatabase(plugin, id);
            try {
                database.init(annotation.entities());
            } catch (StorageInitException e) {
                throw new ServiceInitException(e);
            }

            databases.put(id, database);
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            clear();
        } catch (IOException e) {
            throw new ServiceReloadException("Closing old database connections failed", e);
        }

        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage(), e.getCause());
        }
    }

    public Database getDatabase(String dbIdentifier) {
        String id = dbIdentifier.toLowerCase();
        if (!databases.containsKey(id)) {
            throw new IllegalArgumentException(id + " is not a valid database identifier");
        }
        return databases.get(id);
    }

    private void clear() throws IOException {
        for (Database database : databases.values()) {
            database.close();
        }
    }
}
