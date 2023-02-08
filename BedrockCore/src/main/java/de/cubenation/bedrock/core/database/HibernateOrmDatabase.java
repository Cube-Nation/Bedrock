package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.DataPersistenceConfig;
import de.cubenation.bedrock.core.exception.DatastoreInitException;
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HibernateOrmDatabase extends AbstractDatabase {

//    private static final HashMap<String, Object> settingsMap = new HashMap<>(){{
//        put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
//        put("hibernate.hikari.minimumIdle", "5");
//        put("hibernate.hikari.maximumPoolSize", "10");
//        put("hibernate.hikari.idleTimeout", "30000");
//        put("hibernate.hikari.dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
//        put("hibernate.hikari.dataSource.user", "sa");
//        put("hibernate.show_sql", "true");
//        put("hibernate.hbm2ddl.auto", "create-drop");
//    }};

    private static final HashMap<String, Object> hiddenDefaultConfig = new HashMap<>(){{
        put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        put("hibernate.hikari.minimumIdle", "5");
        put("hibernate.hikari.maximumPoolSize", "10");
        put("hibernate.hikari.idleTimeout", "30000");
//        put("hibernate.hikari.dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
//        put("hibernate.hikari.dataSource.url", "jdbc:mysql://localhost:3306/minecraft");
//        put("hibernate.hikari.dataSource.user", "root");
//        put("hibernate.hikari.dataSource.password", "root");
        put("hibernate.show_sql", "true");
        put("hibernate.hbm2ddl.auto", "create-drop");
    }};

    private SessionFactory sessionFactory;

    public HibernateOrmDatabase(FoundationPlugin plugin, String identifier) {
        super(plugin, identifier);
    }

    public void init(Class<?>... entities) throws DatastoreInitException {
        try {
            initDatabase(entities);
        } catch (ServiceException e) {
            if (e.getCause() instanceof HibernateException && e.getCause().getCause() instanceof IllegalArgumentException) {
                plugin.log(Level.SEVERE, "Datastore could not be initialized: Config or credentials are invalid!");
                plugin.disable();
            } else {
                throw new DatastoreInitException(e);
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
     * Get config map of {@link #hiddenDefaultConfig} merged with actual values from {@link DataPersistenceConfig}
     * @return config map
     */
    private Map<String, Object> getConfigMap() {
        DataPersistenceConfig config = (DataPersistenceConfig) plugin.getConfigService().getConfig(DataPersistenceConfig.class);
        HashMap<String, Object> configMap = new HashMap<>(hiddenDefaultConfig);
        configMap.putAll(config.getDatabaseConfigMapOrInit(identifier));
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
