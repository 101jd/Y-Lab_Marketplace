package org.y_lab.application.service.interfaces;

import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

public interface MarketPlaceService {

    /**
     * Add item
     * @param item
     * @return
     */
    Long addItem(Item item) throws SQLException;
    Item editProduct(Long id, Item item) throws ProductNotFoundException;
    boolean deleteProduct(Item item) throws ProductNotFoundException;
    List<Item> getAllProducts();
    Item findById(Long itemId) throws ProductNotFoundException;
    List<Item> filter(Predicate<? super Item> predicate);
    Item setDiscount(Long uuid, int discount) throws ProductNotFoundException;
}
