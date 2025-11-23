package org.y_lab.adapter.out.repository.marketplace;

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
import org.y_lab.adapter.out.repository.MarketPlaceRepository;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Testcontainers
public class MarketPlaceRepositoryTest {

    @Container
    private static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("mpdbtest")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(false);

    private static Repository<Long, Item> itemRepository;

    private static Connection connection;
    @BeforeAll
    public static void init() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
        );

        connection.prepareStatement("CREATE SCHEMA IF NOT EXISTS mps").executeUpdate();

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        database.setDefaultSchemaName("mps");
        database.setLiquibaseSchemaName("mps");


        Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(),
                database);

        liquibase.update();

        itemRepository = new MarketPlaceRepository(connection);

        connection.setAutoCommit(false);
        connection.createStatement().executeUpdate("ALTER SEQUENCE products_id_seq RESTART WITH 2");

    }

    @DisplayName("Get all method returns List with 1 preloaded item")
    @Test
    public void testGetAll() throws SQLException {
        Assertions.assertEquals(1, itemRepository.getAll().size());
    }

    @DisplayName("Save new item")
    @Test
    public void testSave() throws QtyLessThanZeroException, SQLException {
        Item item = new Item(new Product("Product", "wawawa", 70.0, 15), 1);

        Assertions.assertEquals(2, itemRepository.save(item));
    }

    @DisplayName("Update item test")
    @Test
    public void testUpdate() throws SQLException, QtyLessThanZeroException {
        Item item = new Item(new Product("Product", "wiwi", 70.0, 15), 10);

        Assertions.assertEquals(10, itemRepository.update(1L, item).getQty());
    }

    @Test
    public void deleteTest() throws SQLException {
        Item item = itemRepository.getById(1L);
        System.out.println(item);
        Assertions.assertTrue(itemRepository.delete(item));
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
