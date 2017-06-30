package de.cubenation.bedrock.bungee.api;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.google.common.base.Preconditions;
import de.cubenation.bedrock.core.database.DatabaseConfiguration;
import de.cubenation.bedrock.core.exception.DatabaseSetupException;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benedikthruschka on 16.05.17.
 */
public abstract class DatabasePlugin extends Plugin {

    private EbeanServer ebean = null;

    protected void setupDatabase(DatabaseConfiguration configuration) throws Exception {
        if (isDatabaseEnabled()) {
            ServerConfig db = new ServerConfig();

            db.setDefaultServer(false);
            db.setRegister(false);
            db.setClasses(getDatabaseClasses());
            db.setName(getDescription().getName());
            configureDbConfig(db, configuration);

            DataSourceConfig ds = db.getDataSourceConfig();

            ds.setUrl(replaceDatabaseString(ds.getUrl()));
            getDataFolder().mkdirs();

            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
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

    public void configureDbConfig(ServerConfig config, DatabaseConfiguration configuration) throws DatabaseSetupException {

        // TODO: tdb
//        // If this configuration is null, try to get the Bedrocks one
//        if (configuration == null) {
//            BedrockPlugin plugin = (BedrockPlugin) Bukkit.getServer().getPluginManager().getPlugin("Bedrock");
//            if (plugin != null) {
//                configuration = plugin.getBedrockDefaults().getDatabaseConfiguration();
//                getLogger().log(Level.INFO, "Will use default Bedrock database configuration.");
//            }
//        }

        // If this configuration is still null, throw
        if (configuration == null) {
            throw new DatabaseSetupException();
        }

        DataSourceConfig ds = new DataSourceConfig();
        ds = configuration.configure(ds);

        if (ds.getDriver().contains("sqlite")) {
            config.setDatabasePlatform(new SQLitePlatform());
            config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        config.setDataSourceConfig(ds);
    }
}

