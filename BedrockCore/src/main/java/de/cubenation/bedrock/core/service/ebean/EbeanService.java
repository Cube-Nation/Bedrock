package de.cubenation.bedrock.core.service.ebean;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.EbeanEntity;
import de.cubenation.bedrock.core.config.DatastoreConfig;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.datastore.StorageCredentials;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EbeanService extends AbstractService {

    private final List<Class<?>> ebeanEntities = new ArrayList<>();

    private final HashMap<String, Database> databases = new HashMap<>();

    public EbeanService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        Arrays.stream(this.getPlugin().getClass().getAnnotationsByType(EbeanEntity.class)).forEach(ebeanEntity -> {
            this.ebeanEntities.add(ebeanEntity.value());
            this.databases.put(ebeanEntity.identifier(), this.setupDatabase(ebeanEntity.identifier()));
        });
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.ebeanEntities.clear();
        this.databases.clear();
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

    private Database setupDatabase(final String identifier) {
        StorageCredentials storageCredentials = ((DatastoreConfig) this.getPlugin().getConfigService().getConfig(DatastoreConfig.class)).getDataStore(identifier);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(storageCredentials.username());
        dataSourceConfig.setPassword(storageCredentials.password());
        dataSourceConfig.setUrl(storageCredentials.url());
        dataSourceConfig.setDriver(storageCredentials.driver());

        DatabaseConfig config = new DatabaseConfig();
        config.setClasses(this.ebeanEntities);
        config.setDataSourceConfig(dataSourceConfig);

        return DatabaseFactory.create(config);
    }

    @SuppressWarnings("unused")
    public Database getDatabase(final String identifier) {
        return this.databases.get(identifier);
    }

    public Database getDatabase() {
        return this.getDatabase("ebean");
    }

}
