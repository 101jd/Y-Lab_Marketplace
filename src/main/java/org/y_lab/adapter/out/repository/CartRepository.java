package org.y_lab.adapter.out.repository;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.out.repository.caches.CartCache;
import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SimpleRepository;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.ProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartRepository implements SimpleRepository<UUID, Cart> {

    private Connection connection;
    private Repository<Long, User> userRepository;
    private Cache<UUID, Cart> cartCache;

    public CartRepository() throws SQLException, LiquibaseException {
        this.connection = ConnectionManager.getInstance().getConnection();
        userRepository = new UserRepositoryImpl();
        this.cartCache = new CartCache();
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

            }catch (SQLException e){
                throw new SQLRuntimeException(e.getMessage());
            }
        }
        return cart.getId();
    }

    @Override
    public Cart getById(Long userId) throws SQLException {

        {
            List<Product> products = new ArrayList<>();

            User user = userRepository.getById(userId);

            try {
                return cartCache.getFromCache(user.getCart().getId());
            } catch (NotFoundException e) {

                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT p.id, p.title, p.description, " +
                                "p.price, p.discount FROM goods g JOIN products p WHERE g.cart_id=?")) {
                    statement.setString(1, user.getCart().getId().toString());

                    ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        products.add(new Product(new ProductDTO(
                                resultSet.getLong("p.id"),
                                resultSet.getString("p.title"),
                                resultSet.getString("p.description"),
                                resultSet.getDouble("p.price"),
                                resultSet.getInt("p.discount")
                        )));
                    }
                }
                Cart cart = new Cart(new CartDTO(user.getCart().getId(), products));
                cartCache.cache(cart);
                return cart;
            }
        }
    }

}
