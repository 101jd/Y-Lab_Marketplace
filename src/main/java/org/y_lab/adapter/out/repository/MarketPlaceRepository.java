package org.y_lab.adapter.out.repository;

import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.ConnectionIsNullException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;


public class MarketPlaceRepository implements Repository<Item> {

    private Connection connection;

    public MarketPlaceRepository(Connection connection) {
        this.connection = connection;
        init(connection);
    }

    public MarketPlaceRepository() throws SQLException {
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
    /////////////////////////////////////////////////////////////////




    //////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param item to save
     * @return id of saved Product
     * @throws SQLException
     */
    @Override
    public UUID save(Item item) throws SQLException {
        Product product = item.getProduct();
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO products (id, title, description, price, discount) " +
                        "VALUES(?, ?, ?, ?, ?)")) {
            statement.setString(1, product.getId().toString());
            statement.setString(2, product.getTitle());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getDiscount());

            try(PreparedStatement itemStatement = connection.prepareStatement(
                    "INSERT INTO items (product_id, qty)\n" +
                            "VALUES (?, ?)\n" +
                            "ON CONFLICT (product_id)\n" +
                            "DO UPDATE SET qty = EXCLUDED.qty;\n"
            )) {
                itemStatement.setString(1, product.getId().toString());
                itemStatement.setInt(2, item.getQty());

                statement.executeUpdate();
                itemStatement.executeUpdate();
            }



            return product.getId();
        } catch (SQLException e){
            throw e;
        }finally {
            connection.endRequest();
        }
    }

    /**
     *
     * @param uuid of modifying Product
     * @param item modified
     * @return modified Product
     * @throws SQLException
     */
    @Override
    public Item update(UUID uuid, Item item) throws SQLException {
        Product product = item.getProduct();
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE items SET qty=? WHERE product_id=?"
        )) {
            statement.setInt(1, item.getQty());
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
        }

        if (product.equals(this.getById(uuid).getProduct())) {
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
            statement.setString(5, uuid.toString());

            statement.executeUpdate();

        } catch (SQLException e){
            throw e;
        }finally {
            connection.endRequest();
        }

        System.out.println("repo update qty: " + item.getQty());
        return item;
    }

    /**
     * Delete by item
     * @param item to delete
     * @return deleted Product
     * @throws SQLException
     */
    @Override
    public Item delete(Item item) throws SQLException {
        Product product = item.getProduct();
        connection.beginRequest();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM products WHERE id=?"
        )) {

            statement.setString(1, product.getId().toString());

            statement.executeUpdate();
            return item;
        }catch (SQLException e){

            throw e;
        }finally {
            connection.endRequest();
        }
    }

    /**
     * Get Item by product_id
     * @param uuid - Product id
     * @return Item by id
     * @throws SQLException
     */
    @Override
    public Item getById(UUID uuid) throws SQLException {
        String sql = """
                        SELECT p.id, p.title, p.description, p.price, p.discount, i.qty
                        FROM items i
                        JOIN products p ON i.product_id = p.id
                        WHERE p.id = ?
                """;
        Item item = null;
        connection.beginRequest();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        Product product = new Product(new ProductDTO(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getDouble("price"),
                                rs.getInt("discount")
                        ));

                        item = new Item(product, rs.getInt("qty"));

                    }
                } catch (QtyLessThanZeroException e) {
                    throw new RuntimeException(e);

                }finally {
                    connection.endRequest();
                }

        }

        return item;
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
                            UUID.fromString(rs.getString("id")),
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

    private void init(Connection connection){
        if (connection == null)
            throw new ConnectionIsNullException();

        try(Statement statement = connection.createStatement()){

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                    id VARCHAR(64) PRIMARY KEY,
                    title VARCHAR(64),
                    description VARCHAR(255),
                    price FLOAT,
                    discount INT);""");
            statement.execute(
                    """
                            CREATE TABLE IF NOT EXISTS goods(
                            id BIGSERIAL PRIMARY KEY,
                            cart_id VARCHAR(64), product_id VARCHAR(64),
                            FOREIGN KEY (product_id)
                            REFERENCES products (id));"""
            );

            statement.execute(
                    """
                            CREATE TABLE IF NOT EXISTS items (
                            product_id VARCHAR(64) PRIMARY KEY,
                            qty INT,
                            FOREIGN KEY (product_id)
                            REFERENCES products(id) ON DELETE CASCADE);"""
            );
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLRuntimeException("Tables wasn't created");
        }
    }
}
