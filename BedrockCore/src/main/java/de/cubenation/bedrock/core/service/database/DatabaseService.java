package de.cubenation.bedrock.core.service.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Database;
import de.cubenation.bedrock.core.database.BedrockDatabase;
import de.cubenation.bedrock.core.database.CustomDatabase;
import de.cubenation.bedrock.core.database.DatabaseConfiguration;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class DatabaseService extends AbstractService {

    private HashMap<Class<? extends CustomDatabase>, CustomDatabase> databases = new HashMap<>();

    public DatabaseService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        try {
            this.registerDatabase(BedrockDatabase.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        initializeCustomDatabases();
    }

    @Override
    public void reload() throws ServiceReloadException {

    }

    private void initializeCustomDatabases() {
        Arrays.stream(this.getPlugin().getClass().getAnnotationsByType(Database.class)).forEach(db -> {
            try {
                this.registerDatabase(db.value());
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    private void registerDatabase(Class<? extends CustomDatabase> clazz) throws InstantiationException {
        CustomDatabase db = this.initiateDatabase(clazz);
        this.databases.put(db.getClass(), db);
    }

    private CustomDatabase initiateDatabase(Class<? extends CustomDatabase> clazz) throws InstantiationException {
        try {
            Constructor<?> constructor = clazz.getConstructor(FoundationPlugin.class);
            return (CustomDatabase) constructor.newInstance(plugin);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InstantiationException("Could not instantiate class " + clazz + ": " + e.getMessage());
        }
    }

    public void setupDatabases(DatabaseConfiguration config) throws Exception {
        for (CustomDatabase db : databases.values()) {
            db.setupDatabase(config);
        }
    }

    public io.ebean.Database getDatabase(Class<? extends CustomDatabase> clazz) {
        CustomDatabase db = this.databases.get(clazz);
        if (db == null) {
            return null;
        }
        return db.getDatabase();
    }
}
