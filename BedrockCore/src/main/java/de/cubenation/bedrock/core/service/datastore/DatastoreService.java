package de.cubenation.bedrock.core.service.datastore;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.DatastoreConfig;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

public class DatastoreService extends AbstractService {

    /**
     * Path to directory containing datasource drivers in the plugins directory.
     */
    private static final String DRIVER_PATH = "config/drivers";
    private final Path driverDirectory = Paths.get(plugin.getFallbackBedrockPlugin().getDataFolder().getAbsolutePath(), DRIVER_PATH);

    private final HashMap<String, HikariDataSource> dataSources = new HashMap<>();

    public DatastoreService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        this.dataSources.clear();

        try {
            Files.createDirectories(driverDirectory);
        } catch (IOException e) {
            throw new ServiceInitException("Could not create driver directory", e);
        }

        loadDrivers();

        DatastoreConfig datastoreConfig = (DatastoreConfig) this.plugin.getConfigService().getConfig(DatastoreConfig.class);
        HashMap<String, StorageCredentials> instances = (HashMap<String, StorageCredentials>) datastoreConfig.getDataStores();

        instances.forEach((identifier, storageCredentials) -> {
            try {
                Class.forName(storageCredentials.driver());
            } catch (ClassNotFoundException e) {
                this.plugin.log(Level.SEVERE, "Could not find datastore driver '" + identifier + "'. Skipping!");
                return; //skips to next forEach iteration
            }

            HikariConfig config = new HikariConfig();
            //config.setDataSourceClassName(storageCredentials.driver());
            config.setJdbcUrl(storageCredentials.url());
            config.setUsername(storageCredentials.username());
            config.setPassword(storageCredentials.password());
            config.setPoolName(identifier);
            config.setMaximumPoolSize(storageCredentials.maxPoolSize());
            config.setMinimumIdle(storageCredentials.minIdleConnections());
            config.setKeepaliveTime(storageCredentials.keepAliveTime());
            config.setConnectionTimeout(storageCredentials.connectionTimeout());
            storageCredentials.properties().forEach(config::addDataSourceProperty);

            dataSources.put(identifier, new HikariDataSource(config));
        });
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

    /**
     * Retrieves the data source associated with the identifier.<br>
     * Returns null if identifier is unknown.
     *
     * @param identifier Identifier of the connection pool
     * @return Data source associated with the identifier
     */
    public HikariDataSource getDataSource(final String identifier) {
        return dataSources.get(identifier);
    }

    /**
     * Retrieves an unused connection from the connection pool associated with the identifier.<br>
     * Returns null if identifier is unknown.
     *
     * @param identifier Identifier of the connection pool
     * @return Connection to the specified data source
     */
    public Connection getConnection(final String identifier) throws SQLException {
        final HikariDataSource dataSource = getDataSource(identifier);

        if (dataSource == null) {
            plugin.log(Level.SEVERE, "Data source '" + identifier + "' is unknown!");
            return null;
        }

        return dataSource.getConnection();
    }

    public StorageType getStorageType(String identifier){
        for (StorageType storageType : StorageType.values()) {
            if (storageType.getPattern().matcher(this.getDataSource(identifier).getJdbcUrl()).group(1).equalsIgnoreCase(storageType.name())) {
                return storageType;
            }
        }

        return null;
    }

    public boolean loadDrivers() throws ServiceInitException {
        if(!Files.isDirectory(driverDirectory)){
            plugin.log(Level.SEVERE, "Could not load data source connectors since the specified directory '" + DRIVER_PATH + "' does not exist inside the plugin's directory.");
            return false;
        }


        try {
            Files.list(driverDirectory).forEach(path -> {
                try (URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()}, getClass().getClassLoader())) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException | IOException e) {
                    plugin.log(Level.SEVERE, "Could not load data source connector: " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new ServiceInitException(e);
        }

        return true;
    }
}
