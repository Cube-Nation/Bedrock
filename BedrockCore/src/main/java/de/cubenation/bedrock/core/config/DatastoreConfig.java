package de.cubenation.bedrock.core.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatastoreConfig extends CustomConfigurationFile {

    public DatastoreConfig(FoundationPlugin plugin) {
        CONFIG_FILE = new File(plugin.getPluginFolder(), getFilename());
    }

    public static String getFilename() {
        return "config/_datastores.yaml";
    }

    public DatastoreConfig() {
        super();
    }

    @Path("datastores")
    @Comment("For help on how to configure your datastores view https://tbd/") // TODO: correct url
    private HashMap<String, Map<String, Object>> datastores = new HashMap<>();

    /**
     * Get the config map for the datastore with a specific identifier. </p>
     * Initialize in case of no config is found for the specified identifier.
     * @param identifier identifier of the datastore
     * @return config map
     */
    public Map<String, Object> getDatastoreConfigMapOrInit(final String identifier) {
        Map<String, Object> configMap = getDatastoreConfigMap(identifier);
        if (configMap == null) {
            // init the datastore config with default values
            configMap = new LinkedHashMap<>(){{
                put("datasource", "JSON");
                put("mount", "data/" + identifier + "/");
            }};
            datastores.put(identifier, configMap);
            try {
                save();
            } catch (InvalidConfigurationException e) {
                // should not happen, but throw it just in case anyway
                throw new RuntimeException(e);
            }
        }
        return configMap;
    }

    /**
     * Get the config map for the database with a specific identifier. </p>
     * Return {@link null} in case of no config is found for the specified identifier.
     * @param identifier identifier of the database
     * @return config map or {@link null}
     */
    public Map<String, Object> getDatastoreConfigMap(final String identifier) {
        return datastores.get(identifier);
    }

}
