package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.config.DatabaseConfig;
import de.cubenation.bedrock.core.exception.StorageInitException;
import de.cubenation.bedrock.core.service.config.ConfigService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HibernateOrmDatabase extends Database {

    @Inject
    private ConfigService configService;

    private static final HashMap<String, Object> hiddenDefaultConfig = new HashMap<>(){{
        put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        put("hibernate.hikari.minimumIdle", "5");
        put("hibernate.hikari.maximumPoolSize", "10");
        put("hibernate.hikari.idleTimeout", "30000");
        put("hibernate.hbm2ddl.auto", "update");
    }};

    private SessionFactory sessionFactory;

    public HibernateOrmDatabase(FoundationPlugin plugin, String identifier) {
        super(plugin, identifier);
    }

    public void init(Class<?>... entities) throws StorageInitException {
        try {
            initDatabase(entities);
        } catch (ServiceException e) {
            if (e.getCause() instanceof HibernateException && e.getCause().getCause() instanceof IllegalArgumentException) {
                plugin.log(Level.SEVERE, "Database could not be initialized: Config or credentials are invalid!");
                plugin.disable();
            } else {
                throw new StorageInitException(e);
            }
        }
    }

    private void initDatabase(Class<?>... entities) {
        int entityCount = entities == null ? 0 : entities.length;

        ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(getConfigMap())
                .build();

        MetadataSources sources = new MetadataSources(standardRegistry);
        if (entityCount > 0) {
            sources.addAnnotatedClasses(entities);
        }

        MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
        metadataBuilder.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);
        Metadata metadata = metadataBuilder.build();

        SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
        sessionFactory = sessionFactoryBuilder.build();
    }

    /**
     * Get config map of {@link #hiddenDefaultConfig} merged with actual values from {@link DatabaseConfig}
     * @return config map
     */
    private Map<String, Object> getConfigMap() {
        DatabaseConfig config = (DatabaseConfig) configService.getConfig(DatabaseConfig.class);
        HashMap<String, Object> configMap = new HashMap<>(hiddenDefaultConfig);
        configMap.putAll(config.getDatabaseConfigMapOrInit(identifier));

        // Reaaaally ugly hack to generate H2 database file on first start... Please don't crucify me
        // TODO: Find real solution instead of this nonsense .-.
        if (
                configMap.get("hibernate.hikari.dataSourceClassName").equals("org.h2.jdbcx.JdbcDataSource") &&
                configMap.get("hibernate.hbm2ddl.auto").equals("update") &&
                !Files.exists(Paths.get(configMap.get("hibernate.hikari.dataSource.url").toString().replace("jdbc:h2:file:", "").replace(";AUTO_SERVER=TRUE", "")))
        ) {
            configMap.put("hibernate.hbm2ddl.auto", "create");
        }

        return configMap;
    }

    @Override
    public Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}
