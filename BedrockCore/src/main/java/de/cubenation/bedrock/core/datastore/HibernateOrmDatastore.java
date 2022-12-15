package de.cubenation.bedrock.core.datastore;

import de.cubenation.bedrock.core.exception.DatastoreInitException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.util.HashMap;

public class HibernateOrmDatastore implements Datastore {

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

    private static final HashMap<String, Object> settingsMap = new HashMap<>(){{
        put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        put("hibernate.hikari.minimumIdle", "5");
        put("hibernate.hikari.maximumPoolSize", "10");
        put("hibernate.hikari.idleTimeout", "30000");
        put("hibernate.hikari.dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
        put("hibernate.hikari.dataSource.url", "jdbc:mysql://localhost:3306/minecraft");
        put("hibernate.hikari.dataSource.user", "root");
        put("hibernate.hikari.dataSource.password", "root");
        put("hibernate.show_sql", "true");
        put("hibernate.hbm2ddl.auto", "create-drop");
    }};

    private SessionFactory sessionFactory;

    public void init(Class<?>... entities) throws DatastoreInitException {
        int entityCount = entities == null ? 0 : entities.length;

        ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settingsMap)
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

    @Override
    public DatastoreType getType() {
        return DatastoreType.SQL;
    }

    @Override
    public DatastoreSession openSession() {
        return new HibernateOrmSession(sessionFactory);
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}
