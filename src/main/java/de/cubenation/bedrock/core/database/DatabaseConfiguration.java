package de.cubenation.bedrock.core.database;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class DatabaseConfiguration {

    private String username = "username";

    private String password = "password";

    private String url = "url";

    private String driver = "com.mysql.jdbc.Driver";

    private String isolation = "SERIALIZABLE";

    public DatabaseConfiguration(String username, String password, String url, String driver, String isolation) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.driver = driver;
        this.isolation = isolation;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getIsolation() {
        return isolation;
    }



    public DataSourceConfig configure(DataSourceConfig ds) {
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setUrl(url);
        ds.setDriver(driver);
        ds.setIsolationLevel(TransactionIsolation.getLevel(isolation));
        return ds;
    }

    @Override
    public String toString() {
        return "DatabaseConfiguration{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                ", driver='" + driver + '\'' +
                ", isolation='" + isolation + '\'' +
                '}';
    }
}
