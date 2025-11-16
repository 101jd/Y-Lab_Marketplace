package org.y_lab.adapter.out.repository;

import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SaveRepo;
import org.y_lab.application.exceptions.ConnectionIsNullException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.ProductDTO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class CartRepo implements SaveRepo<Cart> {

    private Connection connection;
    private Repository<User> userRepository;

    public CartRepo() throws SQLException {
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
        userRepository = UserRepositoryImpl.getInstance();
    }

    @Override
    public UUID save(Cart cart) throws SQLException {
        for (Product product : cart.getProducts()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO goods (cart_id, product_id) VALUES(?, ?)"
            )) {
                statement.setString(1, cart.getId().toString());
                statement.setString(2, product.getId().toString());
                 statement.executeUpdate();
            }
        }
        return cart.getOwner_id();
    }

    @Override
    public Cart getById(UUID uuid) throws SQLException {
        List<Product> products = new ArrayList<>();

        User user = userRepository.getById(uuid);


        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT p.id, p.title, p.description, " +
                        "p.price, p.discount FROM goods g JOIN products p WHERE g.cart_id=?")) {
            statement.setString(1, user.getCart().getId().toString());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                products.add(new Product(new ProductDTO(
                        UUID.fromString(resultSet.getString("p.id")),
                        resultSet.getString("p.title"),
                        resultSet.getString("p.description"),
                        resultSet.getDouble("p.price"),
                        resultSet.getInt("p.discount")
                )));
            }
        }
        return new Cart(new CartDTO(user.getCart().getId(), products, uuid));
    }


    //region privates
    private void init(Connection connection){
        //TODO delete souts and drop table
        if (connection == null)
            throw new ConnectionIsNullException();

        try(Statement statement = connection.createStatement()){
//            String addresses = "CREATE TABLE IF NOT EXISTS addresses(" +
//                    "id VARCHAR(64) PRIMARY KEY, city VARCHAR(32), street VARCHAR(32), " +
//                    "houseNumber INT, apartment INT" +
//                    ")";

//            statement.execute(addresses);

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

    //endregion
}
