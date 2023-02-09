package de.cubenation.bedrock.core.service.datastore;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Datastore;
import de.cubenation.bedrock.core.database.Database;
import de.cubenation.bedrock.core.database.HibernateOrmDatabase;
import de.cubenation.bedrock.core.datastore.AbstractDatastore;
import de.cubenation.bedrock.core.datastore.JsonDatastore;
import de.cubenation.bedrock.core.exception.DatastoreInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class DatastoreService extends AbstractService {

    private final HashMap<String, AbstractDatastore<?>> datastores = new HashMap<>();

    public DatastoreService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        for (Datastore datastore : plugin.getClass().getAnnotationsByType(Datastore.class)) {
            try {
                registerDatastore(datastore);
            } catch (DatastoreInitException e) {
                throw new ServiceInitException(e);
            }
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        // TODO: ReloadPolicies
    }

    public void registerDatastore(de.cubenation.bedrock.core.annotation.Datastore annotation) throws DatastoreInitException {
        String id = annotation.name();
        if (id == null || StringUtils.isBlank(id)) {
            // TODO: Sanitize
            throw new DatastoreInitException("Datastore identifier cannot be blank or null");
        }

        Class<?> value = annotation.value();
        if (value == null) {
            throw new DatastoreInitException("Datastore class cannot be null");
        }

        registerDatastore(id, value);
    }

    public void registerDatastore(String identifier, Class<?> entityClass) {
        plugin.log(Level.INFO, String.format("Initializing datastore '%s'...", identifier));

        AbstractDatastore<?> datastore = new JsonDatastore<>(plugin, identifier, entityClass);

        datastores.put(identifier, datastore);
    }

    public <Entity> AbstractDatastore<Entity> getDatastore(String identifier) {
        AbstractDatastore<?> datastore = datastores.get(identifier);

        // TODO: Type Checking

        return (AbstractDatastore<Entity>) datastore;
    }
}
