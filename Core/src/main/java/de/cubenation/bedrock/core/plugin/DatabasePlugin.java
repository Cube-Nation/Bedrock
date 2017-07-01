package de.cubenation.bedrock.core.plugin;

import com.avaje.ebean.config.ServerConfig;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.database.DatabaseConfiguration;
import de.cubenation.bedrock.core.exception.DatabaseSetupException;

import java.util.List;

public interface DatabasePlugin extends FoundationPlugin {

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    List<Class<?>> getDatabaseClasses();

    void setupDatabase(DatabaseConfiguration configuration) throws Exception;

    void configureDbConfig(ServerConfig config, DatabaseConfiguration configuration) throws DatabaseSetupException;

    void installDDL();

    void removeDDL();

    String replaceDatabaseString(String input);

    Boolean isDatabaseEnabled();
}
