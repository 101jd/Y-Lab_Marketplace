package org.y_lab.application.service;

import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.y_lab.adapter.out.repository.MarketPlaceRepository;
import org.y_lab.adapter.out.repository.UserRepositoryImpl;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PlatformServiceImplTest {
    @Mock
    Repository<Long, User> userRepository;

    @Mock
    Repository<Long, Item> itemRepository;

    @InjectMocks
    Service service;

    Item item;

    public PlatformServiceImplTest() throws SQLException, QtyLessThanZeroException, LiquibaseException {
        itemRepository = new MarketPlaceRepository();
        userRepository = new UserRepositoryImpl();
        this.service = new PlatformServiceImpl();
        item = new Item(new Product("Test", "tst", 10.0, 2), 3);
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @DisplayName("Update with non-existing uuid throws Product not found exception")
    @Test
    public void testThrows() throws SQLException, QtyLessThanZeroException {
        Long uuid = 10L;
        Mockito.when(itemRepository.update(uuid, item)).thenThrow(new SQLException());
        Assertions.assertThrows(ProductNotFoundException.class, () -> service.editProduct(
                new User("a", "a", null, false), uuid, item));
    }

    @DisplayName("Get all products returns list of all products")
    @Test
    public void testGetAll() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));
        Assertions.assertArrayEquals(service.getAllProducts().toArray(), List.of(item).toArray());
    }

    @DisplayName("Filter returns products with totalprice over 10")
    @Test
    public void testFilterOver10() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));

        Assertions.assertArrayEquals(service.filter(i -> i.getProduct().getTotalPrice() > 10).toArray(), List.of().toArray());
    }

    @DisplayName("Filter returns products with totalprice under 10")
    @Test
    public void testFilterUnder10() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));

        Assertions.assertEquals(service.filter(i -> i.getProduct().getTotalPrice() <= 10).get(0), item);
    }

}
