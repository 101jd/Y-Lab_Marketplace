package org.y_lab.application.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    Repository<User> userRepository;

    @Mock
    Repository<Item> itemRepository;

    @InjectMocks
    Service service;

    Item item;

    public PlatformServiceImplTest() throws SQLException, QtyLessThanZeroException {
        itemRepository = new MarketPlaceRepository();
        userRepository = UserRepositoryImpl.getInstance();
        this.service = new PlatformServiceImpl();
        item = new Item(new Product("Test", "tst", 10.0, 2), 3);
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testThrows() throws SQLException, QtyLessThanZeroException {
        UUID uuid = UUID.randomUUID();
        Mockito.when(itemRepository.update(uuid, item)).thenThrow(new SQLException());
        Assertions.assertThrows(ProductNotFoundException.class, () -> service.editProduct(uuid, item));
    }

    @Test
    public void testGetAll() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));
        Assertions.assertArrayEquals(service.getAllProducts().toArray(), List.of(item).toArray());
    }

    @Test
    public void testFilterUnder10() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));

        Assertions.assertArrayEquals(service.filter(i -> i.getProduct().getTotalPrice() <= 5).toArray(), List.of().toArray());
    }

    @Test
    public void testFilterOver10() throws SQLException {
        Mockito.when(itemRepository.getAll()).thenReturn(List.of(item));

        Assertions.assertEquals(service.filter(i -> i.getProduct().getTotalPrice() <= 10).get(0), item);
    }

}
