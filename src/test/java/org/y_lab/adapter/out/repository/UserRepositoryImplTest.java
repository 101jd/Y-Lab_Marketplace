package org.y_lab.adapter.out.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y_lab.application.model.*;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.UserDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class UserRepositoryImplTest {
    UserRepositoryImpl userRepository;
    Connection connection;
    UUID userId = UUID.randomUUID();
    UUID addressId = UUID.randomUUID();
    UUID cartId = UUID.randomUUID();
    User user;

    @BeforeEach
    public void init() throws SQLException {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:h2:mem:testmpdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH", "sa", ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userRepository = new UserRepositoryImpl(connection);
        insert();
    }

    @Test
    public void testSave() throws SQLException {
        User user = new User("test", "test",
                new Address("test", "test", 1, 2), true);

        Assertions.assertEquals(userRepository.save(user), user.getId());
    }

    @Test
    public void testGetById() throws SQLException {
        Assertions.assertEquals(userRepository.getById(userId), user);
    }

    @Test
    public void testGetAll() throws SQLException {
        Assertions.assertArrayEquals(userRepository.getAll().toArray(), List.of(user).toArray());
    }

    @Test
    public void testUpdate() throws SQLException {
        User u1 = new User(new UserDTO(userId, "A", "123", new Address(
                "Acity", "Astreet", 7, 26
        ), new Cart(new CartDTO(UUID.randomUUID(), new ArrayList<>(), userId)), true));

        userRepository.update(userId, u1);

        Assertions.assertEquals(userRepository.getById(userId), u1);
    }

    @Test
    public void testDelete() throws SQLException {
        userRepository.delete(user);

        Assertions.assertArrayEquals(userRepository.getAll().toArray(), Collections.EMPTY_LIST.toArray());
    }


    @AfterEach
    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insert() {
        UserDTO dto = new UserDTO(userId, "Test", "test",
                new Address(
                        new AddressDTO(addressId, "testCity", "testStreet", 11, 2)
                ),
                new Cart(new CartDTO(cartId, new ArrayList<>(), userId)), true);
        User p = new User(dto);
        this.user = p;

        if (connection != null) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO addresses (id, city, street, houseNumber, apartment) " +
                            "VALUES(?, ?, ?, ?, ?);"
            )) {
                Address address = user.getAddress();
                statement.setString(1, address.getId().toString());
                statement.setString(2, address.getCity());
                statement.setString(3, address.getStreet());
                statement.setInt(4, address.getHouseNumber());
                statement.setInt(5, address.getApartment());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO carts (id, user_id) VALUES(?, ?);"
            )){
                Cart cart = user.getCart();
                statement.setString(1, cart.getId().toString());
                statement.setString(2, user.getId().toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (" +
                    "id, username, password, address_id, cart_id) " +
                    "VALUES(?, ?, ?, ?, ?);")) {
                statement.setString(1, user.getId().toString());
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getPassword().toString());
                statement.setString(4, user.getAddress().getId().toString());
                statement.setString(5, user.getCart().getId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }
    }
}
