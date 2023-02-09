package de.cubenation.bedrock.core.service.datastore;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.datastore.Datastore;
import de.cubenation.bedrock.core.datastore.JsonDatastore;
import de.cubenation.bedrock.core.datastore.ReloadPolicy;
import de.cubenation.bedrock.core.exception.DatastoreInitException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DatastoreService extends AbstractService {

    private final HashMap<String, Datastore<?>> datastores = new HashMap<>();
    private final HashMap<String, ReloadPolicy> reloadPolices = new HashMap<>();

    public DatastoreService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        for (de.cubenation.bedrock.core.annotation.Datastore datastore : plugin.getClass().getAnnotationsByType(de.cubenation.bedrock.core.annotation.Datastore.class)) {
            try {
                registerDatastore(datastore);
            } catch (DatastoreInitException e) {
                throw new ServiceInitException(e);
            }
        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        for(Map.Entry<String, Datastore<?>> entry : datastores.entrySet()) {
            String key = entry.getKey();
            Datastore<?> datastore = entry.getValue();
            ReloadPolicy reloadPolicy = reloadPolices.get(key);

            if (reloadPolicy == ReloadPolicy.RELOAD) {
                try {
                    datastore.reloadAll();
                } catch (IOException e) {
                    throw new ServiceReloadException(e);
                }
            } else if (reloadPolicy == ReloadPolicy.UNLOAD) {
                datastore.unloadAll();
            }
        }
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
        reloadPolices.put(id, annotation.reloadPolicy());
    }

    public void registerDatastore(String identifier, Class<?> entityClass) {
        plugin.log(Level.INFO, String.format("Initializing datastore '%s'...", identifier));

        Datastore<?> datastore;

        datastore = new JsonDatastore<>(plugin, identifier, entityClass);

        datastores.put(identifier, datastore);
    }

    public <Entity> Datastore<Entity> getDatastore(String identifier) {
        Datastore<?> datastore = datastores.get(identifier);

        // TODO: Type Checking

        return (Datastore<Entity>) datastore;
    }
}
