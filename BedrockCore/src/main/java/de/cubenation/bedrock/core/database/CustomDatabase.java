package de.cubenation.bedrock.core.database;

import com.google.common.base.Preconditions;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.DatabaseEntity;
import de.cubenation.bedrock.core.exception.DatabaseSetupException;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.sqlite.SQLitePlatform;
import io.ebean.datasource.DataSourceConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public abstract class CustomDatabase {

    private final FoundationPlugin plugin;
    private Database ebean = null;

    @Getter
    private boolean databaseEnabled = false;

    @Getter
    private final List<Class<?>> databaseEntities = new ArrayList<>();

    public CustomDatabase(FoundationPlugin plugin) {
        this.plugin = plugin;
        this.initiateDatabaseEntities();

        if (this.databaseEntities.size() > 0) {
            this.databaseEnabled = true;
        }
    }

    public abstract String getName();

    public String getFullName() {
        if (this.getName() == null) {
            return plugin.getPluginDescription().getName();
        }
        return plugin.getPluginDescription().getName()+"_"+this.getName();
    }

    public void setupDatabase(DatabaseConfiguration configuration) throws Exception {
        if (isDatabaseEnabled()) {
            DatabaseConfig cfg = new DatabaseConfig();

            // Basic db config
            cfg.setDefaultServer(false);
            cfg.setRegister(false);
            cfg.setClasses(getDatabaseEntities());
            cfg.setName(this.getFullName());

            // Set config properties
            this.configureDbConfig(cfg, configuration);

            // Replace url placeholders
            DataSourceConfig ds = cfg.getDataSourceConfig();
            ds.setUrl(replaceDatabaseString(ds.getUrl()));
            plugin.getDataFolder().mkdirs();

            // Use main ClassLoader when creating db
            ebean = DatabaseFactory.createWithContextClassLoader(cfg, plugin.getClass().getClassLoader());
        }
    }

    private void initiateDatabaseEntities() {
        Arrays.stream(this.getClass().getAnnotationsByType(DatabaseEntity.class)).forEach(dbEntity -> {
            databaseEntities.add(dbEntity.value());
        });
    }

    public String replaceDatabaseString(String input) {
        input = input.replaceAll("\\{DIR\\}", plugin.getDataFolder().getPath().replaceAll("\\\\", "/") + "/");
        input = input.replaceAll("\\{NAME\\}", plugin.getPluginDescription().getName().replaceAll("[^\\w_-]", ""));
        return input;
    }

    public Database getDatabase() {
        Preconditions.checkState(isDatabaseEnabled(), "Database is not enabled.");
        return ebean;
    }

    public void configureDbConfig(DatabaseConfig config, DatabaseConfiguration configuration) throws DatabaseSetupException {

        // ToDo: Inheritance, other property values, etc.
        // Don't do this for Bedrock Plugin Instance
        if (!plugin.isFallbackBedrockPlugin()) {
            // If this configuration is null, try to get the Bedrocks one
            if (configuration == null) {
                FoundationPlugin fallback = plugin.getFallbackBedrockPlugin();
                if (fallback != null) {
                    configuration = fallback.getBedrockDefaults().getDatabaseConfiguration();
                    plugin.log(Level.INFO, "Will use default Bedrock database configuration.");
                }
            }
        }

        // If this configuration is still null, throw
        if (configuration == null) {
            throw new DatabaseSetupException();
        }

        DataSourceConfig ds = new DataSourceConfig();
        ds = configuration.configure(ds);

        if (ds.getDriver().contains("sqlite")) {
            config.setDatabasePlatform(new SQLitePlatform());
            // ToDo: Is that needed? Anyway.
            //config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        config.setDataSourceConfig(ds);
    }
}
