package org.y_lab.application.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SaveRepository;
import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Platform;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
public class PlatformServiceImplTest {
    @Mock
    Repository<Long, User> userRepository;

    @Mock
    Repository<Long, Item> itemRepository;

    @Mock
    SaveRepository<UUID, Cart> cartSaveRepository;

    @Mock
    Platform platform;

    @InjectMocks
    PlatformServiceImpl service;

    Item item;


    @BeforeEach
    public void init() throws QtyLessThanZeroException{
        initMocks(this);
        item = new Item(new Product("Test", "tst", 10.0, 2), 3);
    }

    @DisplayName("Update with non-existing uuid throws Product not found exception")
    @Test
    public void testThrows() throws SQLException, QtyLessThanZeroException {
        Long id = 10L;
        Mockito.when(itemRepository.update(id, item)).thenThrow(new SQLException());
        Assertions.assertThrows(ProductNotFoundException.class, () -> service.editProduct(id, item));
    }



    @DisplayName("Get all products returns list of all products")
    @Test
    public void testGetAll() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));
        List<Item> items = service.getAllProducts();

        Assertions.assertEquals(items, List.of(item));
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

        List<Item> items = service.filter(i -> i.getProduct().getTotalPrice() < 10.0);

        Assertions.assertEquals(items, List.of(item));
    }

}
