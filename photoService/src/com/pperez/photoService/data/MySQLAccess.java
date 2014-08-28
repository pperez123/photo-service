/**
 * 
 */
package com.pperez.photoService.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pperez
 * @version Aug 22, 2014
 * <p>MySQLAccess.java</p>
 */
public class MySQLAccess {
    private final Logger logger = LoggerFactory.getLogger(MySQLAccess.class);
    private final String username = "photosite";
    private final String password = "photosite";
    
    protected String defaultDB = "photosite";
    protected Connection connection = null;
    
    public Connection openDatabase() throws Exception {
        if (connection != null) {
            closeDatabase();
        }
        
        Context initContext = new InitialContext();
        Context envContext  = (Context) initContext.lookup("java:/comp/env");
        
        if (envContext != null) {
            DataSource datasource = (DataSource) envContext.lookup("jdbc/" + defaultDB);
            connection = datasource.getConnection();
        } else {
            logger.warn("No environmental context. Cannot get DB connection");
        }
        
        return connection;
    }
    
    public Connection openDatabase(String database) throws Exception {
        if (database == null) {
            database = defaultDB;
        }
        
        if (connection != null) {
            closeDatabase();
        }

        // this will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");
        // setup the connection with the DB.
        connection = DriverManager.getConnection("jdbc:mysql://localhost/" + database + "?user=" + username + "&password=" + password);

        if (connection != null) {
            logger.debug("Successful connection to " + database);
        }

        return connection;
    }
    
    public void closeDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            logger.warn("Close database failed: " + e.getMessage());
            // don't throw now as it might leave following closeables in undefined state
        }
    }
    
    public void closePreparedStatement(PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            logger.warn("Close statement failed: " + e.getMessage());
            // don't throw now as it might leave following closeables in undefined state
        }
    }
    
    public void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception e) {
            logger.warn("Close result set failed: " + e.getMessage());
            // don't throw now as it might leave following closeables in undefined state
        }
    }
}
