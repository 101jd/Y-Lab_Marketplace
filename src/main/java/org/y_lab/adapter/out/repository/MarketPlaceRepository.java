package org.y_lab.adapter.out.repository;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.out.repository.caches.ItemCache;
import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MarketPlaceRepository implements Repository<Long, Item> {

    private Connection connection;
    private Cache<Long, Item> itemCache;



    public MarketPlaceRepository(Connection connection){
        this.connection = connection;
        this.itemCache = new ItemCache();
    }

    public MarketPlaceRepository() throws SQLException, LiquibaseException {
        this(ConnectionManager.getInstance().getConnection());

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
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO products (title, description, price, discount) " +
                        "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getTitle());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getDiscount());

            statement.executeUpdate();

            ResultSet set = statement.getGeneratedKeys();

            Long id = null;

            if (set.next()){
                id = set.getLong("id");
            } else throw new SQLRuntimeException("Item save failed");

            try(PreparedStatement itemStatement = connection.prepareStatement(
                    "INSERT INTO items (product_id, qty)\n" +
                            "VALUES (?, ?)\n" +
                            "ON CONFLICT (product_id)\n" +
                            "DO UPDATE SET qty = EXCLUDED.qty;\n"
            )) {
                itemStatement.setLong(1, id);
                itemStatement.setInt(2, item.getQty());

                itemStatement.executeUpdate();


                return id;

            }
        } catch (SQLException e){
            throw e;
        }finally {
            connection.endRequest();
        }
    }

    /**
     *
     * @param id of modifying Product
     * @param item modified
     * @return modified Product
     * @throws SQLException
     */
    @Override
    public Item update(Long id, Item item) throws SQLException {
        Product product = item.getProduct();
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE items SET qty=? WHERE product_id=?"
        )) {
            statement.setInt(1, item.getQty());
            statement.setLong(2, id);

            statement.executeUpdate();
        }

        if (product.equals(this.getById(id).getProduct())) {
            return item;
        }

        try(PreparedStatement statement = connection.prepareStatement(
                """
                        UPDATE products SET title=?, description=?,
                        price=?, discount=?
                        WHERE id=?"""
        )) {

            statement.setString(1, product.getTitle());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getDiscount());
            statement.setLong(5, id);

            statement.executeUpdate();

        } catch (SQLException e){
            throw e;
        }finally {
            connection.endRequest();
        }

        try {
            Item i = new Item(new Product(new ProductDTO(id, product.getTitle(), product.getDescription(),
                    product.getPrice(), product.getDiscount())), item.getQty());
            itemCache.cache(i);
            return i;
        } catch (QtyLessThanZeroException e) {
            throw new RuntimeException(e);
        }
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
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM products WHERE id=?"
        )) {

            statement.setLong(1, product.getId());

            statement.executeUpdate();
            itemCache.delete(product.getId());
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            connection.endRequest();
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
            return itemCache.fromCache(id);
        }catch (NotFoundException e) {
            String sql = """
                            SELECT p.id, p.title, p.description, p.price, p.discount, i.qty
                            FROM items i
                            JOIN products p ON i.product_id = p.id
                            WHERE p.id = ?
                    """;
            Item item = null;
            connection.beginRequest();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        Product product = new Product(new ProductDTO(
                                rs.getLong("id"),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getDouble("price"),
                                rs.getInt("discount")
                        ));

                        item = new Item(product, rs.getInt("qty"));
                        itemCache.cache(item);

                    }
                } catch (QtyLessThanZeroException ex) {
                    throw new RuntimeException(ex);

                } finally {
                    connection.endRequest();
                }


            }

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

        connection.beginRequest();
        List<Item> items = new ArrayList<>();
        String sql = """
                        SELECT p.id, p.title, p.description, p.price, p.discount, i.qty
                        FROM items i
                        JOIN products p ON i.product_id = p.id;
                """;


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(new ProductDTO(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("discount")
                    ));

                    items.add(new Item(product, rs.getInt("qty")));

                }
            } catch (QtyLessThanZeroException e) {

                throw new RuntimeException(e);
            }finally {
                connection.endRequest();
            }
        }

        return items;
    }
}
