package org.y_lab.adapter.out.repository;

import org.junit.jupiter.api.*;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MarketplaceRepositoryTest {


    private MarketPlaceRepository repository;
    private Connection connection;
    private UUID id = UUID.randomUUID();
    private Product product;
    private Item item;


    @BeforeEach
    public void init() throws QtyLessThanZeroException {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:h2:mem:testmpdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH", "sa", ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (connection != null)
            try(Statement statement = connection.createStatement()){
                statement.execute("CREATE TABLE IF NOT EXISTS products (" +
                        "id VARCHAR(64) PRIMARY KEY, " +
                        "title VARCHAR(64), " +
                        "description VARCHAR(255), " +
                        "price FLOAT, " +
                        "discount INT);");
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        this.repository = new MarketPlaceRepository(connection);
        insert();
    }


    private void insert() throws QtyLessThanZeroException {
        ProductDTO dto = new ProductDTO(id, "Test", "test", 1.0, 50);
        Product p = new Product(dto);
        product = p;
        item = new Item(product, 1);

        if (connection != null)
            try(PreparedStatement statement = connection.prepareStatement("INSERT INTO products (" +
                    "id, title, description, price, discount) " +
                    "VALUES(?, ?, ?, ?, ?);")){
                statement.setString(1, product.getId().toString());
                statement.setString(2, product.getTitle());
                statement.setString(3, product.getDescription());
                statement.setDouble(4, product.getPrice());
                statement.setInt(5, product.getDiscount());
                statement.execute();
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    @Test
    @Disabled
    public void getAllTest() throws SQLException {
        Assertions.assertArrayEquals(List.of(new Product(
                        new ProductDTO(id, "Test", "test", 1.0, 50)))
                .toArray(), repository.getAll().toArray());


    }

    @Test
    @Disabled
    public void testSave() throws SQLException, QtyLessThanZeroException {
        UUID id = UUID.randomUUID();
        String title = "Test";
        String description = "test";
        double price = 1.0;
        int discount = 50;

        ProductDTO dto = new ProductDTO(id, title, description, price, discount);
        Item item = new Item(new Product(dto), 10);

        UUID result = repository.save(item);

        Assertions.assertEquals(result, id);
    }

    @Test
    @Disabled
    public void getByIdTest() throws SQLException {
        Assertions.assertEquals(repository.getById(id).getProduct(), product);
    }

    @Test
    public void deteteTest() throws SQLException {
        repository.delete(item);
        Assertions.assertEquals(Collections.EMPTY_LIST, repository.getAll());
    }

    @AfterEach
    public void close() throws SQLException {
        connection.close();
    }


}
