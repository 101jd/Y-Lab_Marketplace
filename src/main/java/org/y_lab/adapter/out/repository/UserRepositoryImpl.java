package org.y_lab.adapter.out.repository;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.out.repository.caches.UserCache;
import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImpl implements Repository<Long, User> {

    private Connection connection;
    private Cache<Long, User> userCache;

    public UserRepositoryImpl(Connection connection){
        this.connection = connection;
        this.userCache = new UserCache();
    }

    public UserRepositoryImpl() throws SQLException, LiquibaseException {
        this(ConnectionManager.getInstance().getConnection());
    }




    @Override
    public Long save(User user) throws SQLException {
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users (username, password, address_id, cart_id, admin) " +
                        "VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
        )) {

            statement.setString(1, user.getUsername());
            statement.setString(2, new String(user.getPassword()));
            statement.setString(3, user.getAddress().getId().toString());
            statement.setString(4, user.getCart().getId().toString());
            statement.setBoolean(5, user.isAdmin());

            saveAddress(user.getAddress());
            saveCart(user);
            statement.executeUpdate();

            ResultSet set = statement.getGeneratedKeys();

            if (set.next()) {
                return set.getLong("id");
            }
            else throw new SQLRuntimeException("User save failed");

        }catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    @Override
    public User update(Long id, User user) throws SQLException {
        connection.beginRequest();
        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET username=?, password=?, address_id=?, admin=? WHERE id=?"
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, new String(user.getPassword()));
            statement.setString(3, user.getAddress().getId().toString());
            statement.setBoolean(4, user.isAdmin());
            statement.setLong(5, id);


            if (!this.getById(id).getAddress().getId().equals(user.getAddress().getId())){
                saveAddress(user.getAddress());
            }

            statement.executeUpdate();

            User u = new User(new UserDTO(id, user.getUsername(),new String(user.getPassword()),
                    user.getAddress(), user.getCart(), user.isAdmin()));

            userCache.cache(u);

            return u;
        } catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    @Override
    public boolean delete(User user) throws SQLException {
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM users WHERE id=?"
        )) {
            statement.setLong(1, user.getId());

            statement.executeUpdate();
            userCache.delete(user.getId());
            return true;
        }catch (SQLException e){
            return false;
        } finally {
            connection.endRequest();
        }
    }

    @Override
    public User getById(Long id) throws SQLException {
        try {
            return userCache.fromCache(id);
        }catch (NotFoundException e)
        {
            connection.beginRequest();

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM users WHERE id=?"
            )) {
                statement.setLong(1, id);

                ResultSet resultSet = statement.executeQuery();

                User user = null;

                if (resultSet.next()) {
                    user = new User(new UserDTO(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            this.getAddressById(UUID.fromString(resultSet.getString("address_id"))),
                            this.getCartById(UUID.fromString(resultSet.getString("cart_id"))),
                            resultSet.getBoolean("admin")
                    ));
                }

                connection.endRequest();
                if (user != null)
                    userCache.cache(user);
                return user;
            }
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
                        rs.getLong("id"),
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
    private UUID saveAddress(Address address) throws SQLException {
        try(PreparedStatement addressStatement = connection.prepareStatement(
                "INSERT INTO addresses (id, city, street, \"houseNumber\", apartment) " +
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
        return address.getId();
    }

    private UUID saveCart(User user) throws SQLException {
        try(PreparedStatement cartStatement = connection.prepareStatement(
                "INSERT INTO carts (id) " +
                        "VALUES(?)"
        )) {
            cartStatement.setString(1, user.getCart().getId().toString());
            cartStatement.executeUpdate();
        }catch (SQLException e){
            throw e;
        }
        return user.getCart().getId();
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
                        new ArrayList<>()));

            }
            return cart;
        } catch (SQLException e){
            throw e;
        } finally {
            connection.endRequest();
        }
    }

    //endregion
}
