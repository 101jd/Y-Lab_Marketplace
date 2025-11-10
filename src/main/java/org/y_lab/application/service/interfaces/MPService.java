package org.y_lab.application.service.interfaces;

import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public interface MPService {

    /**
     * Add item
     * @param item
     * @return
     */
    UUID addItem(Item item);
    Item editProduct(UUID id, Item item);
    Item deleteProduct(Item item);
    List<Item> getAllProducts();
    Item findById(UUID id);
    List<Item> filter(Predicate<? super Item> predicate);
    Item setDiscount(UUID uuid, int discount);
}
