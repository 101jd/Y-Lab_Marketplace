package org.y_lab.application.model;

import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.CartDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cart {
    private UUID id;
    private List<Product> products;
    private UUID owner_id;

    public Cart(CartDTO dto){
        this.id = dto.getId();
        this.products = dto.getProducts();
        this.owner_id = dto.getOwner_id();
    }

    public Cart(User owner) {
        this.id = UUID.randomUUID();
        this.products = new ArrayList<>();
        this.owner_id = owner.getId();
    }

    public UUID getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public UUID getOwner_id() {
        return owner_id;
    }

    public Item addProduct(Item item){
        this.products.add(item.getProduct());
        return item;
    }
}
