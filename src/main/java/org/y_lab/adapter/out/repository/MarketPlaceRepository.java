package org.y_lab.adapter.out.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.y_lab.adapter.out.repository.caches.ItemCache;
import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@org.springframework.stereotype.Repository
public class MarketPlaceRepository implements Repository<Long, Item> {

    private JdbcTemplate template;
    private Cache<Long, Item> itemCache;



    @Autowired
    public MarketPlaceRepository(JdbcTemplate template){
        this.template = template;
        this.itemCache = new ItemCache();
    }


    /**
     *
     * @param item to save
     * @return id of saved Product
     * @throws SQLException
     */
    @Override
    public Long save(Item item) throws SQLException {
        Product product = item.getProduct();
        String sqlProducts = "INSERT INTO products (title, description, price, discount) " +
                        "VALUES(?, ?, ?, ?)";
        String sqlItems = "INSERT INTO items (product_id, qty)\n" +
                            "VALUES (?, ?)\n" +
                            "ON CONFLICT (product_id)\n" +
                            "DO UPDATE SET qty = EXCLUDED.qty;\n";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlProducts, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getTitle());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getDiscount());
            return statement;
        }, keyHolder);

        Long productId = (Long) keyHolder.getKeys().get("id");
        template.update(sqlItems, productId, item.getQty());
        itemCache.cache(item);

        return productId;
    }

    /**
     *
     * @param item modified
     * @return modified Product
     * @throws SQLException
     */
    @Override
    public Item update(Item item) throws SQLException {
        Long id = item.getProduct().getId();
        Product product = item.getProduct();
        String sqlItem = "UPDATE items SET qty=? WHERE product_id=?";
        String sqlProduct = """
                        UPDATE products SET title=?, description=?,
                        price=?, discount=?
                        WHERE id=?""";

        template.update(sqlItem, item.getQty(), id);
        template.update(sqlProduct,product.getTitle(), product.getDescription(),
                product.getPrice(), product.getDiscount(), id);
        itemCache.cache(item);

        return item;
    }

    /**
     * Delete by item
     *
     * @param item to delete
     * @return deleted Product
     * @throws SQLException
     */
    @Override
    public boolean delete(Item item) throws SQLException {
        Product product = item.getProduct();
        String sql = "DELETE FROM products WHERE id=?";

        try {
            template.update(sql, item.getProduct().getId());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Get Item by product_id
     * @param id - Product id
     * @return Item by id
     * @throws SQLException
     */
    @Override
    public Item getById(Long id) throws SQLException {
        try {
            return itemCache.getFromCache(id);
        }catch (NotFoundException e) {
            String sql = """
                            SELECT p.id, p.title, p.description, p.price, p.discount, i.qty
                            FROM items i
                            JOIN products p ON i.product_id = p.id
                            WHERE p.id = ?
                    """;

            Item item = template.query(sql, rs -> {
                try {
                    rs.next();
                    Item i = new Item(
                            new Product(new ProductDTO(
                                    rs.getLong("id"),
                                    rs.getString("title"),
                                    rs.getString("description"),
                                    rs.getDouble("price"),
                                    rs.getInt("discount")
                            )), rs.getInt("qty")
                    );

                    return i;
                } catch (QtyLessThanZeroException ex) {
                    throw new RuntimeException(ex);
                }
            }, id);

            itemCache.cache(item);
            return item;
            }
        }


    /**
     *
     * @return List of all Products in DB (can be empty)
     * @throws SQLException
     */
    @Override
    public List<Item> getAll() throws SQLException {
        String sql = """
                        SELECT p.id, p.title, p.description, p.price, p.discount, i.qty
                        FROM items i
                        JOIN products p ON i.product_id = p.id;
                """;

        RowMapper mapper = (rs, rowNum) -> {
            Item item = null;
            try {
                item = new Item(new Product(
                        new ProductDTO(
                                rs.getLong("id"),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getDouble("price"),
                                rs.getInt("discount")
                        )
                ), rs.getInt("qty"));
                return item;
            } catch (QtyLessThanZeroException e) {
                throw new RuntimeException(e);
            }
        };

        return template.query(sql, mapper);

    }
}
