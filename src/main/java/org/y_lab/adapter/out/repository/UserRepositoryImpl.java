package org.y_lab.adapter.out.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.y_lab.adapter.out.repository.caches.UserCache;
import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.UserDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Repository
public class UserRepositoryImpl implements Repository<Long, User> {


    private JdbcTemplate template;
    private Cache<Long, User> userCache;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate template){
        this.template = template;
        this.userCache = new UserCache();
    }

    @Override
    public Long save(User user) throws SQLException {

        String sql = "INSERT INTO users (username, password, address_id, cart_id, admin) " +
                "VALUES(?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        saveAddress(user.getAddress());
        saveCart(user);

        template.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, new String(user.getPassword()));
            statement.setString(3, user.getAddress().getId().toString());
            statement.setString(4, user.getCart().getId().toString());
            statement.setBoolean(5, user.isAdmin());


            return statement;
        }, keyHolder);



        Long id = keyHolder.getKeyAs(Long.class);
        userCache.cache(new User(new UserDTO(
                id, user.getUsername(), new String(user.getPassword()),
                user.getAddress(), user.getCart(), user.isAdmin())));
        return id;
    }

    @Override
    public User update(User user) throws SQLException {
        Long id = user.getId();
        String sql = "UPDATE users SET username=?, password=?, address_id=?, admin=? WHERE id=?";


        template.update(sql, user.getUsername(), new String(user.getPassword()),
                user.getAddress().getId().toString(), user.isAdmin());

        saveAddress(user.getAddress());

        return user;

    }

    @Override
    public boolean delete(User user) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try {
            template.update(sql, user.getId());
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public User getById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try {
            return userCache.getFromCache(id);
        }catch (NotFoundException e)
        {
            User user = template.query(sql, rs -> {
                return new User(new UserDTO(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        getAddressById(UUID.fromString(rs.getString("address_id"))),
                        getCartById(UUID.fromString(rs.getString("cart_id"))),
                        rs.getBoolean("admin")
                ));
            });
            return user;
            }
        }


    @Override
    public List<User> getAll() throws SQLException {

        String sql = """
        SELECT u.id, u.username, u.password,
               a.id AS address_id, a.city, a.street, a."houseNumber", a.apartment
        FROM users u
        JOIN addresses a ON u.address_id = a.id
        """;

        return template.query(sql, (rs, rowNum) -> {
            Address address = new Address(new AddressDTO(
                    UUID.fromString(rs.getString("address_id")),
                    rs.getString("city"),
                    rs.getString("street"),
                    rs.getInt("houseNumber"),
                    rs.getInt("apartment")
            ));

            return new User(new UserDTO(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    address,
                    new Cart(),
                    rs.getBoolean("admin")
            ));
        });

    }

    //region private methods
    private UUID saveAddress(Address address) throws SQLException {
        String sql =  "INSERT INTO addresses (id, city, street, \"houseNumber\", apartment) " +
                "VALUES(?, ?, ?, ?, ?)";

        try {
            template.update(sql, address.getId().toString(), address.getCity(), address.getStreet(),
                    address.getHouseNumber(), address.getApartment());
        }catch (DataAccessException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return address.getId();
    }

    private UUID saveCart(User user) throws SQLException {
        String sql = "INSERT INTO carts (id) " +
                "VALUES(?)";

        template.update(sql, user.getCart().getId());
        return user.getCart().getId();
    }

    private Address getAddressById(UUID id) throws SQLException {
        String sql = "SELECT * FROM addresses WHERE id=?";

        return template.query(sql, rs -> {
            return new Address(new AddressDTO(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("city"),
                    rs.getString("street"),
                    rs.getInt("houseNumber"),
                    rs.getInt("apartment")
            ));
        });

    }

    private Cart getCartById(UUID id) throws SQLException {
        String sql = "SELECT * FROM carts WHERE id=?";

        return template.query(sql, rs -> {
            return new Cart(new CartDTO(
                    UUID.fromString(rs.getString("id")),
                    new ArrayList<>()
            ));
        });
    }

    //endregion
}
