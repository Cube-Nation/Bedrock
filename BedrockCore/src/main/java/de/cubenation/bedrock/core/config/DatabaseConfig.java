package de.cubenation.bedrock.core.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.util.*;

public class DatabaseConfig extends CustomConfigurationFile {

    public DatabaseConfig(FoundationPlugin plugin) {
        CONFIG_FILE = new File(plugin.getPluginFolder(), getFilename());
    }

    public static String getFilename() {
        return "config/_databases.yaml";
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
     * Default values for initialization of new database configs.
     */
    private static final Map<String, Object> defaultConfigMap = new LinkedHashMap<>() {{
        put("datasource", "org.h2.jdbcx.JdbcDataSource");
        put("url", "jdbc:h2:file:./bedrock;AUTO_SERVER=TRUE");
        put("username", "minecraft");
        put("password", UUID.randomUUID()); // better than nothing
    }};

    @Path("databases")
    @Comment("For help on how to configure your databases view https://tbd/") // TODO: correct url
    private HashMap<String, Map<String, Object>> databases = new HashMap<>();

    /**
     * Get the config map for the database with a specific identifier. </p>
     * Initialize in case of no config is found for the specified identifier.
     * @param identifier identifier of the database
     * @return config map
     */
    public Map<String, Object> getDatabaseConfigMapOrInit(final String identifier) {
        Map<String, Object> configMap = getDatabaseConfigMap(identifier);
        if (configMap == null) {
            // init the database config with default values
            configMap = new LinkedHashMap<>(defaultConfigMap);
            databases.put(identifier, configMap);
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
    public Map<String, Object> getDatabaseConfigMap(final String identifier) {
        return replaceReservedKeywords(databases.get(identifier));
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
