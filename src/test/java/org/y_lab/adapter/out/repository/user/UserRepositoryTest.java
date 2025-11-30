package org.y_lab.adapter.out.repository.user;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.y_lab.adapter.out.repository.UserRepositoryImpl;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Testcontainers
public class UserRepositoryTest {
    @Container
    private static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("mpdbtest")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(false);

    private static Connection connection;

    private static Repository<Long, User> userRepository;

    @BeforeAll
    public static void init() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
        );

        connection.prepareStatement("CREATE SCHEMA IF NOT EXISTS mps").executeUpdate();

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                new JdbcConnection(connection)
        );

        database.setDefaultSchemaName("mps");
        database.setLiquibaseSchemaName("mps");

        Liquibase liquibase = new Liquibase("db/changelog/changelog.xml",
                new ClassLoaderResourceAccessor(), database);

        liquibase.update();

        connection.createStatement().executeUpdate("ALTER SEQUENCE users_id_seq RESTART WITH 2");

        userRepository = new UserRepositoryImpl(connection);

    }


    @DisplayName("Get all returns 1 preloaded user")
    @Test
    public void testGetAll() throws SQLException {
        Assertions.assertEquals(1, userRepository.getAll().size());
    }

    @DisplayName("Saves new user")
    @Test
    public void testSave() throws SQLException {
        User user = new User("qwerty", "123",
                new Address("w", "asd", 11, 2), true);

        Assertions.assertEquals(2, userRepository.save(user));
    }

    @DisplayName("Updates user data")
    @Test
    public void testUpdate() throws SQLException {
        User user = new User("qwerty", "123",
                new Address("w", "asd", 11, 2), true);

        Assertions.assertEquals("qwerty", userRepository.update(1L, user).getUsername());
    }

    @DisplayName("Deletes user")
    @Test
    public void testDelete() throws SQLException {
        User user = userRepository.getById(1L);
        Assertions.assertTrue(userRepository.delete(user));
    }

    @AfterEach
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @AfterAll
    public static void close() throws SQLException {
        connection.close();
        container.close();
    }
}
