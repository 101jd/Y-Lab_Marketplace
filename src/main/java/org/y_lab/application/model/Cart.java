package org.y_lab.application.model;

import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.CartDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  Domain class Cart
 */
public class Cart {
    private UUID id;
    private List<Product> products;

    /**
     * Constructor from DTO object
     * @param dto to copy fields
     */
    public Cart(CartDTO dto){
        this.id = dto.getId();
        this.products = dto.getProducts();
    }

    /**
     * Base constructor
     */
    public Cart() {
        this.id = UUID.randomUUID();
        this.products = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    /**
     *
     * @return copy of products List
     */
    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public Item addProduct(Item item){
        this.products.add(item.getProduct());
        return item;
    }

}
