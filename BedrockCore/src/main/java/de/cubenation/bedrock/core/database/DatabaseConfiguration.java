/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
