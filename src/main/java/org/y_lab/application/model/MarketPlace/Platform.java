package org.y_lab.application.model.MarketPlace;

import org.y_lab.application.model.Cart;

import java.util.ArrayList;
import java.util.List;

/**
 * Marketplace platform class
 * releases products to user's carts
 */
public class Platform {

    List<Item> items;

    public Platform() {

        items = new ArrayList<>();
    }

    public void addItem(Item item){
        items.add(item);

    }

    /**
     * decrements qty of item in marketplace
     * @param item to release
     * @param cart of buyer
     * @return modified item
     */
    public Item releaseProductToCart(Item item, Cart cart){
        if (item.getQty() > 0) {
            cart.addProduct(item);
            item.decrementQty();
        }
        return item;
    }
}
