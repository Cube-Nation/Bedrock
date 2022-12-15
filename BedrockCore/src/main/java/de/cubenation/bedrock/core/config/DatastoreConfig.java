package de.cubenation.bedrock.core.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.helper.CastUtil;
import de.cubenation.bedrock.core.service.datastore.StorageCredentials;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DatastoreConfig extends CustomConfigurationFile {

    public DatastoreConfig(FoundationPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder(), getFilename());
    }

    public static String getFilename() {
        return "datastore.yml";
    }

    private static final ArrayList<String> reservedKeywords = new ArrayList<>() {{
        add("driver");
        add("url");
        add("username");
        add("password");
        add("maxpoolsize");
        add("minidleconnections");
        add("keepalivetime");
        add("connectiontimeout");
    }};

    public DatastoreConfig() {
        super();
    }

    @Path("datastores")
    private HashMap<String, HashMap<String, String>> dataStores = new HashMap<>() {{
        put("myDatabase", new HashMap<>() {{
            put("driver", "com.mysql.jdbc.Driver");
            put("url", "jdbc:mysql:127.0.0.1:3306/minecraft");
            put("username", "root");
            put("password", "root");
            put("maxpoolsize", "16");
            put("minidleconnections", "1");
            put("keepalivetime", "60000");
            put("connectiontimeout", "5000");
        }});
    }};

    /**
     * Retrieves datastore as HashMap.
     *
     * @param identifier Identifier of the datastore
     * @return Datastore as Map
     */
    public Map<String, String> getDataStoreAsMap(final String identifier) {
        return dataStores.get(identifier);
    }

    /**
     * Retrieve all specified connections as {@link StorageCredentials}.
     *
     * @return Map of StorageCredentials
     */
    public Map<String, StorageCredentials> getDataStores() {
        return dataStores.keySet().stream().map(
                e -> new AbstractMap.SimpleEntry<>(e, getDataStore(e))
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    /**
     * @param identifier Identifier of the datastore.
     * @return Datastore as {@link StorageCredentials}
     */
    public StorageCredentials getDataStore(final String identifier) {
        final HashMap<String, String> ds = dataStores.get(identifier);

        if (ds == null) {
            return null;
        }

        return new StorageCredentials(
                ds.get("driver"),
                ds.get("url"),
                ds.get("username"),
                ds.get("password"),
                CastUtil.fromStringToInt(ds.get("maxpoolsize"), 16),
                CastUtil.fromStringToInt(ds.get("minidleconnections"), 1),
                CastUtil.fromStringToInt(ds.get("keepalivetime"), 60000),
                CastUtil.fromStringToInt(ds.get("connectiontimeout"), 5000),
                getDatastoreProperties(identifier)
        );
    }

    /**
     * Filters out all non-reserved keywords, e.g. the extra properties for the JDBC driver.
     *
     * @param identifier Identifier of the datastore
     * @return Map containing only JDBC properties
     */
    private Map<String, String> getDatastoreProperties(final String identifier) {
        final Map<String, String> dataStore = getDataStoreAsMap(identifier);

        if (dataStore == null) {
            return null;
        }

        return dataStore.entrySet().stream().filter(e -> !reservedKeywords.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
