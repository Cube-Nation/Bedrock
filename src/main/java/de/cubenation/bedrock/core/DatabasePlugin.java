package de.cubenation.bedrock.core;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.google.common.base.Preconditions;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.database.DatabaseConfiguration;
import de.cubenation.bedrock.core.exception.DatabaseSetupException;
import de.cubenation.bedrock.plugin.BedrockPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by benedikthruschka on 16.05.17.
 */
public interface DatabasePlugin {

    Boolean isDatabaseEnabled();

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    List<Class<?>> getDatabaseClasses();

    EbeanServer getDatabase();

    void configureDbConfig(ServerConfig config, BedrockDefaults bedrockDefaults) throws DatabaseSetupException;
}
