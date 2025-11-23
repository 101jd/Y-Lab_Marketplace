package org.y_lab.application.service.interfaces;

import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.User;

import java.util.List;
import java.util.function.Predicate;

public interface MarketPlaceService {

    /**
     * Add item
     * @param item
     * @return
     */
    Long addItem(User user, Item item);
    Item editProduct(User user, Long id, Item item);
    boolean deleteProduct(User user, Item item);
    List<Item> getAllProducts();
    Item findById(User user, Long itemId);
    List<Item> filter(Predicate<? super Item> predicate);
    Item setDiscount(Long uuid, int discount);
}
