package org.y_lab.adapter.out.repository;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.y_lab.application.exceptions.ConnectionIsNullException;
import org.y_lab.application.exceptions.SQLRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private Connection connection;
    private static ConnectionManager instance;

    private ConnectionManager() throws SQLException, LiquibaseException {
        Properties properties = new Properties();
        try {
            InputStream loader = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("application.properties");
            properties.load(loader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Connection connection = DriverManager.getConnection(
                properties.getProperty("url"), properties.getProperty("username"),
                properties.getProperty("password"));

        this.connection = connection;

        //костыль, но работает
        connection.prepareStatement("CREATE SCHEMA IF NOT EXISTS mps AUTHORIZATION postgres;").executeUpdate();

        JdbcConnection jdbcConnection = new JdbcConnection(this.connection);
        System.out.println(jdbcConnection.getConnectionUserName());
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(jdbcConnection);



        database.setDefaultSchemaName("mps");
        database.setLiquibaseSchemaName("mps");
        database.setDatabaseChangeLogTableName("databasechangelog");
        database.setDatabaseChangeLogLockTableName("databasechangeloglock");

        Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(),
                database);
        liquibase.update();
    }


    public static ConnectionManager getInstance() throws SQLException, LiquibaseException {
        if (instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }
}
