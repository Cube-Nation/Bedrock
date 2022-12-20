package de.cubenation.bedrock.core.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.DatastoreInitException;
import de.cubenation.bedrock.core.helper.CastUtil;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DatastoreConfig extends CustomConfigurationFile {

    public DatastoreConfig(FoundationPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), getFilename());
    }

    public static String getFilename() {
        return "datastore.yml";
    }

    /**
     * Reserved keywords to shorten some of the most common config options.
     */
    private static final Map<String, String> reservedKeywords = new HashMap<>() {{
        put("datasource", "hibernate.hikari.dataSourceClassName");
        put("url", "hibernate.hikari.dataSource.url");
        put("username", "hibernate.hikari.dataSource.user");
        put("password", "hibernate.hikari.dataSource.password");
    }};

    /**
     * Default values for initialization of new datastore configs.
     */
    private static final Map<String, Object> defaultConfigMap = new LinkedHashMap<>() {{
        put("datasource", "");
        put("url", "");
        put("username", "");
        put("password", "");
    }};

    public DatastoreConfig() {
        super();
    }

    @Path("datastores")
    @Comment("For help on how to configure your datastores view https://tbd/") // TODO: correct url
    private HashMap<String, Map<String, Object>> dataStores = new HashMap<>();

    /**
     * Get the config map for the datasource with a specific identifier. </p>
     * Initialize in case of no config is found for the specified identifier.
     * @param identifier identifier of the datasource
     * @return config map
     */
    public Map<String, Object> getDataSourceConfigMapOrInit(final String identifier) {
        Map<String, Object> configMap = getDataSourceConfigMap(identifier);
        if (configMap == null) {
            // init the datastore config with default values
            configMap = new LinkedHashMap<>(defaultConfigMap);
            dataStores.put(identifier, configMap);
            try {
                save();
            } catch (InvalidConfigurationException e) {
                // should not happen, but throw it just in case anyways
                throw new RuntimeException(e);
            }
        }
        return configMap;
    }

    /**
     * Get the config map for the datasource with a specific identifier. </p>
     * Return {@link null} in case of no config is found for the specified identifier.
     * @param identifier identifier of the datasource
     * @return config map or {@link null}
     */
    public Map<String, Object> getDataSourceConfigMap(final String identifier) {
        return replaceReservedKeywords(dataStores.get(identifier));
    }

    /**
     * Replaces all reserved keywords of a config map with their full-length equivalents.
     * @param input config map
     * @return config map without reserved keywords
     */
    private static Map<String, Object> replaceReservedKeywords(final Map<String, Object> input) {
        if (input == null) {
            return null;
        }

        HashMap<String, Object> output = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String key = reservedKeywords.containsKey(entry.getKey()) ?
                    reservedKeywords.get(entry.getKey()) :
                    entry.getKey();
            output.put(key, entry.getValue());
        }
        return output;
    }
}
