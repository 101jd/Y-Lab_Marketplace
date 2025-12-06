package org.y_lab.adapter.out.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.config.WebAppConfig;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootTest
@Testcontainers
@Import(WebAppConfig.class)
public class UserRepositoryTest {

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
    private UserRepositoryImpl userRepository;

    @BeforeAll
    public static void start() throws IOException, InterruptedException {
        postgres.start();
        postgres.execInContainer("psql", "-U", "postgres", "-c",
                "CREATE SCHEMA IF NOT EXISTS mps");
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
        User user = new User(new UserDTO(1l, "testuser", "123",
                new Address("w", "asd", 11, 2), new Cart(), true));

        Assertions.assertEquals("testuser", userRepository.update(user).getUsername());
    }


    @DisplayName("Deletes user")
    @Test
    public void testDelete() throws SQLException {
        Assertions.assertTrue(userRepository.delete(new User(new UserDTO(1L, "testuser", "123",
                new Address("w", "asd", 11, 2), new Cart(), true))));
    }

    @AfterAll
    public static void stop(){
        postgres.stop();
    }
}