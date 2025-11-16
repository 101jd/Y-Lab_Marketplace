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
        System.out.println(properties.getProperty("password"));
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

    private void init(Connection connection) throws SQLException {
        if (connection == null)
            throw new ConnectionIsNullException();

//        try(Statement statement = connection.createStatement()){
//            String addresses = "CREATE TABLE IF NOT EXISTS addresses(" +
//                    "id VARCHAR(64) PRIMARY KEY, city VARCHAR(32), street VARCHAR(32), " +
//                    "houseNumber INT, apartment INT" +
//                    ")";
//
//            statement.execute(addresses);
//
//            String users = "CREATE TABLE IF NOT EXISTS users (" +
//                    "id VARCHAR(64) PRIMARY KEY, " +
//                    "username VARCHAR(64) UNIQUE, " +
//                    "password VARCHAR(255), " +
//                    "address_id VARCHAR(64), " +
//                    "cart_id VARCHAR(64)," +
//                    "admin BOOL,\n" +
//                    "FOREIGN KEY (address_id)\n" +
//                    "REFERENCES addresses(id)\n" +
//                    "ON DELETE CASCADE)";
//
//            statement.execute(users);
//
//            String carts = "CREATE TABLE IF NOT EXISTS carts (" +
//                    "id VARCHAR(64) PRIMARY KEY, user_id VARCHAR(64))";
//            statement.execute(carts);
//
//            String goods = "CREATE TABLE IF NOT EXISTS goods (" +
//                    "id BIGSERIAL PRIMARY KEY, " +
//                    "cart_id VARCHAR(64), product_id VARCHAR(64), " +
//                    "FOREIGN KEY (cart_id) " +
//                    "REFERENCES carts (id))";
//            statement.execute(goods);
//
//            statement.execute("""
//                    CREATE TABLE IF NOT EXISTS products (
//                    id VARCHAR(64) PRIMARY KEY,
//                    title VARCHAR(64),
//                    description VARCHAR(255),
//                    price FLOAT,
//                    discount INT);""");
//
//            statement.execute(
//                    """
//                            CREATE TABLE IF NOT EXISTS items (
//                            product_id VARCHAR(64) PRIMARY KEY,
//                            qty INT,
//                            FOREIGN KEY (product_id)
//                            REFERENCES products(id) ON DELETE CASCADE);"""
//            );
//
//            statement.execute("""
//                    CREATE TABLE IF NOT EXISTS audition (
//                    id VARCHAR(64) PRIMARY KEY,
//                    user_id VARCHAR(64),
//                    time TIMESTAMP,
//                    message VARCHAR(255));
//                    """);
//
//
//        }catch (SQLException e) {
//            e.printStackTrace();
//            throw new SQLRuntimeException("Tables wasn't created");
//        }
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
