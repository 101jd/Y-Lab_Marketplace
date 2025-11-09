package org.y_lab.application.model.dto;

import org.y_lab.application.model.MarketPlace.Product;

import java.util.List;
import java.util.UUID;

public class CartDTO {
    private UUID id;
    private List<Product> products;
    private UUID owner_id;

    public CartDTO(UUID id, List<Product> products, UUID owner_id) {
        this.id = id;
        this.products = products;
        this.owner_id = owner_id;
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
}
