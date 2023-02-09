package de.cubenation.bedrock.core.datastore;

import de.cubenation.bedrock.core.FoundationPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;

@AllArgsConstructor
public abstract class AbstractDatastore<Entity> {

    protected final FoundationPlugin plugin;

    @Getter
    public final String identifier;
    protected final Class<Entity> entityClass;

    protected final HashMap<String, Entity> entityInstances = new HashMap<>();

    public void load(String key) throws IOException {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        executeLoad(key);
    }

    abstract void executeLoad(String key) throws IOException;

    public void unload(String key) {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        entityInstances.remove(key);
    }

    public void persist(String key, Entity value) throws IOException {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Stored entity cannot be null");
        }
        executePersist(key, value);
    }

    abstract void executePersist(String key, Entity value) throws IOException;

    public Entity get(String key) {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        return entityInstances.get(key);
    }
}
