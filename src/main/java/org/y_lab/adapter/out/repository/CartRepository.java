package org.y_lab.adapter.out.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SimpleRepository;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Repository
public class CartRepository implements SimpleRepository<UUID, Cart> {

    private JdbcTemplate template;
    private Repository<Long, User> userRepository;
    private Cache<UUID, Cart> cartCache;


    @Autowired
    public CartRepository(Repository<Long, User> userRepository, Cache<UUID, Cart> cartCache, JdbcTemplate template) {
        this.userRepository = userRepository;
        this.cartCache = cartCache;
        this.template = template;
    }

    @Override
    public UUID save(Cart cart) throws SQLException {
        String sql = "INSERT INTO goods (cart_id, product_id) VALUES(?, ?)";
        for (Product product : cart.getProducts())
            template.update(sql, cart.getId(), product.getId());

        return cart.getId();
    }

    @Override
    public Cart getById(Long userId) throws SQLException {

        User user = userRepository.getById(userId);
        String sql = "SELECT p.id, p.title, p.description, " +
                                "p.price, p.discount FROM goods g JOIN products p WHERE g.cart_id=?";

        RowMapper<Product> mapper = (rs, rowNum) -> {
            Product product = new Product(new ProductDTO(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("discount")
            ));

            return product;
        };

        List<Product> products = template.query(sql, mapper, user.getCart().getId());

        Cart cart = new Cart( new CartDTO(user.getCart().getId(), products));

        return cart;
    }

}
