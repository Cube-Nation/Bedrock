package de.cubenation.bedrock.core.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cubenation.bedrock.core.FoundationPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonDatastore<Entity> extends AbstractDatastore<Entity> {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonDatastore(FoundationPlugin plugin, String identifier, Class<Entity> entityClass) {
        super(plugin, identifier, entityClass);
    }

    @Override
    void executeLoad(String key) throws IOException {
        Path path = getPathForKey(key);
        if (!Files.notExists(path)) {
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
        Path path = Paths.get(pluginFolder, "data", identifier, fileName);
        path.getParent().toFile().mkdirs();
        return path;
    }
}
