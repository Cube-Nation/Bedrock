package de.cubenation.bedrock.core.datastore;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.injection.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;

public abstract class Datastore<Entity> extends Component {

    @Getter
    public final String identifier;
    protected final Class<Entity> entityClass;

    protected final HashMap<String, Entity> entityInstances = new HashMap<>();

    public Datastore(FoundationPlugin plugin, String identifier, Class<Entity> entityClass) {
        super(plugin);
        this.identifier = identifier;
        this.entityClass = entityClass;
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
        executeUnload(key);
    }

    private void executeUnload(String key) {
        entityInstances.remove(key);
    }

    public void unloadAll() {
        for (String s : entityInstances.keySet()) {
            executeUnload(s);
        }
    }

    public void reload(String key) throws IOException {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        executeReload(key);
    }

    private void executeReload(String key) throws IOException {
        //executeUnload(key);
        executeLoad(key);
    }

    public void reloadAll() throws IOException {
        for (String key : entityInstances.keySet()) {
            executeReload(key);
        }
    }

    public Entity get(String key) {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        return entityInstances.get(key);
    }

    public boolean exists(String key) {
        if (key == null || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Storage key cannot be blank");
        }
        return entityInstances.containsKey(key);
    }
}
