package org.y_lab.adapter.out.repository;

import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.ConnectionIsNullException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.*;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.UserDTO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class UserRepositoryImpl implements Repository<User> {

    private Connection connection;
    private static Repository<User> instance;

    static {
        try {
            instance = new UserRepositoryImpl();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public UserRepositoryImpl(Connection connection) throws SQLException {
        this.connection = connection;
        init(connection);
    }

    private UserRepositoryImpl() throws SQLException {
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
        init(connection);
    }


    @Override
    public UUID save(User user) throws SQLException {
        connection.beginRequest();
        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users (id, username, password, address_id, cart_id, admin) " +
                        "VALUES(?, ?, ?, ?, ?, ?)"
        )) {
            statement.setString(1, user.getId().toString());
            statement.setString(2, user.getUsername());
            statement.setString(3, new String(user.getPassword()));
            statement.setString(4, user.getAddress().getId().toString());
            statement.setString(5, user.getCart().getId().toString());
            statement.setBoolean(6, user.isAdmin());

            saveAddress(user.getAddress());
            saveCart(user);
            statement.executeUpdate();

            return user.getId();

        }catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    @Override
    public User update(UUID uuid, User user) throws SQLException {
        connection.beginRequest();
        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET username=?, password=?, address_id=?, admin=? WHERE id=?"
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, new String(user.getPassword()));
            statement.setString(3, user.getAddress().getId().toString());
            statement.setBoolean(4, user.isAdmin());
            statement.setString(5, uuid.toString());


            if (!this.getById(uuid).getAddress().getId().equals(user.getAddress().getId())){
                saveAddress(user.getAddress());
            }

            statement.executeUpdate();

            return user;
        } catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    @Override
    public User delete(User user) throws SQLException {
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM users WHERE id=?"
        )) {
            statement.setString(1, user.getId().toString());

            statement.executeUpdate();
            return user;
        }catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    @Override
    public User getById(UUID uuid) throws SQLException {
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM users WHERE id=?"
        )) {
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();

            User user = null;

            if (resultSet.next()){
                user = new User(new UserDTO(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        this.getAddressById(UUID.fromString(resultSet.getString("address_id"))),
                        this.getCartById(UUID.fromString(resultSet.getString("cart_id"))),
                        resultSet.getBoolean("admin")
                ));
            }

            connection.endRequest();
            return user;
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        connection.beginRequest();
        List<User> users = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM users"
        )) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                User user = new User(new UserDTO(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("username"),
                        rs.getString("password"),
                        this.getAddressById(UUID.fromString(rs.getString("address_id"))),
                        this.getCartById(UUID.fromString(rs.getString("cart_id"))),
                        rs.getBoolean("admin")
                ));

                users.add(user);

            }
            connection.endRequest();
            return users;

        }catch (SQLException e){
            throw e;
        }
    }

    //region private methods
    private void saveAddress(Address address) throws SQLException {
        try(PreparedStatement addressStatement = connection.prepareStatement(
                "INSERT INTO addresses (id, city, street, houseNumber, apartment) " +
                        "VALUES(?, ?, ?, ?, ?)"
        )) {
            addressStatement.setString(1, address.getId().toString());
            addressStatement.setString(2, address.getCity());
            addressStatement.setString(3, address.getStreet());
            addressStatement.setInt(4, address.getHouseNumber());
            addressStatement.setInt(5, address.getApartment());
            addressStatement.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }
    }

    private void saveCart(User user) throws SQLException {
        try(PreparedStatement cartStatement = connection.prepareStatement(
                "INSERT INTO carts (id, user_id) " +
                        "VALUES(?, ?)"
        )) {
            cartStatement.setString(1, user.getCart().getId().toString());
            cartStatement.setString(2, user.getId().toString());
            cartStatement.executeUpdate();
        }catch (SQLException e){
            throw e;
        }

    }

    private Address getAddressById(UUID id) throws SQLException {
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM addresses WHERE id=?"
        )) {
            statement.setString(1, id.toString());

            ResultSet resultSet = statement.executeQuery();

            Address address = null;
            if (resultSet.next()){
                address = new Address(new AddressDTO(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("city"),
                        resultSet.getString("street"),
                        resultSet.getInt("houseNumber"),
                        resultSet.getInt("apartment")
                ));

            }
            return address;
        } catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    private Cart getCartById(UUID id) throws SQLException {
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM carts WHERE id=?"
        )) {
            statement.setString(1, id.toString());

            ResultSet resultSet = statement.executeQuery();

            Cart cart = null;
            if (resultSet.next()){
                cart = new Cart(new CartDTO(
                        UUID.fromString(resultSet.getString("id")),
                        new ArrayList<>(),
                        UUID.fromString(resultSet.getString("user_id"))));

            }
            return cart;
        } catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    private void init(Connection connection){
        //TODO delete souts and drop table
        if (connection == null)
            throw new ConnectionIsNullException();

        try(Statement statement = connection.createStatement()){
            String addresses = "CREATE TABLE IF NOT EXISTS addresses(" +
                    "id VARCHAR(64) PRIMARY KEY, city VARCHAR(32), street VARCHAR(32), " +
                    "houseNumber INT, apartment INT" +
                    ")";

            statement.execute(addresses);

//            statement.execute("DROP TABLE users");

            String users = "CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(64) PRIMARY KEY, " +
                    "username VARCHAR(64) UNIQUE, " +
                    "password VARCHAR(255), " +
                    "address_id VARCHAR(64), " +
                    "cart_id VARCHAR(64)," +
                    "admin BOOL,\n" +
                    "FOREIGN KEY (address_id)\n" +
                    "REFERENCES addresses(id)\n" +
                    "ON DELETE CASCADE)";

            statement.execute(users);

            String carts = "CREATE TABLE IF NOT EXISTS carts (" +
                    "id VARCHAR(64) PRIMARY KEY, user_id VARCHAR(64))";
            statement.execute(carts);

            String goods = "CREATE TABLE IF NOT EXISTS goods (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "cart_id VARCHAR(64), product_id VARCHAR(64), " +
                    "FOREIGN KEY (cart_id) " +
                    "REFERENCES carts (id))";
            statement.execute(goods);


        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLRuntimeException("Table products wasn't created");
        }
    }

    public static Repository<User> getInstance() {
        return instance;
    }

    //endregion
}
