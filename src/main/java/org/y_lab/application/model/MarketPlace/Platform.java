package org.y_lab.application.model.MarketPlace;

import org.y_lab.application.model.Cart;

import java.util.ArrayList;
import java.util.List;

public class Platform {

    List<Item> items;

    public Platform() {

        items = new ArrayList<>();
    }

    public void addItem(Item item){
        items.add(item);

    }

    public Item releaseProductToCart(Item item, Cart cart){
        if (item.getQty() > 0) {
            cart.addProduct(item);
            item.decrementQty();
        }
        return item;
    }
}
