package org.y_lab.application.model.dto;

import jakarta.annotation.Nullable;
import org.y_lab.application.model.MarketPlace.Product;

import java.util.List;
import java.util.UUID;

public class CartDTO {
    @Nullable
    private UUID id;
    @Nullable
    private List<Product> products;

    public CartDTO(UUID id, List<Product> products) {
        this.id = id;
        this.products = products;
    }

    public UUID getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

}
