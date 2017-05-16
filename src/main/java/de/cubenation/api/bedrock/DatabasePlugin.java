package de.cubenation.api.bedrock;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.google.common.base.Preconditions;
import de.cubenation.api.bedrock.config.BedrockDefaults;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benedikthruschka on 16.05.17.
 */
public abstract class DatabasePlugin extends JavaPlugin {

    private EbeanServer ebean = null;

    protected void setupDatabase(BedrockDefaults bedrockDefaults) throws Exception {
        if (isDatabaseEnabled()) {
            ServerConfig db = new ServerConfig();

            db.setDefaultServer(false);
            db.setRegister(false);
            db.setClasses(getDatabaseClasses());
            db.setName(getDescription().getName());
            configureDbConfig(db, bedrockDefaults);

            DataSourceConfig ds = db.getDataSourceConfig();

            ds.setUrl(replaceDatabaseString(ds.getUrl()));
            getDataFolder().mkdirs();

            ClassLoader previous = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader(getClassLoader());
            ebean = EbeanServerFactory.create(db);
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    public abstract Boolean isDatabaseEnabled();

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>();
    }

    private String replaceDatabaseString(String input) {
        input = input.replaceAll("\\{DIR\\}", getDataFolder().getPath().replaceAll("\\\\", "/") + "/");
        input = input.replaceAll("\\{NAME\\}", getDescription().getName().replaceAll("[^\\w_-]", ""));
        return input;
    }

    public EbeanServer getDatabase() {
        Preconditions.checkState(isDatabaseEnabled(), "Plugin does not have database: true");

        return ebean;
    }

    protected void installDDL() {
        SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(false, gen.generateCreateDdl());
    }

    protected void removeDDL() {
        SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(true, gen.generateDropDdl());
    }

    public void configureDbConfig(ServerConfig config, BedrockDefaults bedrockDefaults) {
        Validate.notNull(config, "Config cannot be null");

        // TODO
        DataSourceConfig ds = new DataSourceConfig();
        ds.setDriver(bedrockDefaults.getDatabaseDriver());
        ds.setUrl(bedrockDefaults.getDatabaseUrl());
        ds.setUsername(bedrockDefaults.getDatabaseUsername());
        ds.setPassword(bedrockDefaults.getDatabasePassword());
        ds.setIsolationLevel(TransactionIsolation.getLevel(bedrockDefaults.getDatabaseIsolation()));

        if (ds.getDriver().contains("sqlite")) {
            config.setDatabasePlatform(new SQLitePlatform());
            config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        config.setDataSourceConfig(ds);
    }
}
