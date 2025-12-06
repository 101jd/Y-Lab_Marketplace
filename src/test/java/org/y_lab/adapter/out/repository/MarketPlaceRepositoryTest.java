package org.y_lab.adapter.out.repository;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.config.WebAppConfig;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootTest
@Testcontainers
@Import(WebAppConfig.class)
public class MarketPlaceRepositoryTest {


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("schema.sql");;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Важно: пусть Liquibase использует эти же параметры
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @Autowired
    private MarketPlaceRepository repository;

    @BeforeAll
    public static void start() throws IOException, InterruptedException {
        postgres.start();
        postgres.execInContainer("psql", "-U", "postgres", "-c",
                "CREATE SCHEMA IF NOT EXISTS mps");
        postgres.execInContainer("psql", "-U", "postgres", "-c",
                "ALTER SEQUENCE mps.products_id_seq RESTART WITH 2");
    }

    @DisplayName("Get all method returns List with 1 preloaded item")
    @Test
    public void testGetAll() throws SQLException {
        Assertions.assertEquals(1, repository.getAll().size());
    }

    @DisplayName("Save new item")
    @Test
    public void testSave() throws QtyLessThanZeroException, SQLException {
        Item item = new Item(new Product("Product", "wawawa", 70.0, 15), 1);

        Assertions.assertEquals(2, repository.save(item));
    }

    @DisplayName("Update item test")
    @Test
    public void testUpdate() throws SQLException, QtyLessThanZeroException {
        Item item = new Item(new Product(
                new ProductDTO(1L, "Product", "wiwi", 70.0, 15)), 10);

        Assertions.assertEquals(10, repository.update(item).getQty());
    }

    @Test
    public void deleteTest() throws SQLException {
        Item item = repository.getById(1L);
        System.out.println(item);
        Assertions.assertTrue(repository.delete(item));
    }

    @AfterAll
    public static void stop(){
        postgres.stop();
    }
}
