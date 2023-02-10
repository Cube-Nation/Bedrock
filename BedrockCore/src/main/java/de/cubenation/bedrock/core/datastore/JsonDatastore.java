package de.cubenation.bedrock.core.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.DatabaseConfig;
import de.cubenation.bedrock.core.config.DatastoreConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonDatastore<Entity> extends Datastore<Entity> {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonDatastore(FoundationPlugin plugin, String identifier, Class<Entity> entityClass) {
        super(plugin, identifier, entityClass);
    }

    @Override
    void executeLoad(String key) throws IOException {
        Path path = getPathForKey(key);
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        File file = path.toFile();

        Entity entity = mapper.readValue(file, entityClass);
        entityInstances.put(key, entity);
    }

    @Override
    void executePersist(String key, Entity value) throws IOException {
        File file = getPathForKey(key).toFile();
        mapper.writeValue(file, value);
    }

    private Path getPathForKey(String key) throws IOException {
        String pluginFolder = plugin.getPluginFolder().getPath();
        String fileName = key + ".json";
        Path path = Paths.get(pluginFolder, getConfigMap().get("mount").toString(), fileName);
        path.getParent().toFile().mkdirs();
        return path;
    }

    /**
     * Get config map merged with actual values from {@link DatabaseConfig}
     * @return config map
     */
    private Map<String, Object> getConfigMap() {
        DatastoreConfig config = (DatastoreConfig) plugin.getConfigService().getConfig(DatastoreConfig.class);
        HashMap<String, Object> configMap = new HashMap<>();
        configMap.putAll(config.getDatastoreConfigMapOrInit(identifier));
        return configMap;
    }
}
