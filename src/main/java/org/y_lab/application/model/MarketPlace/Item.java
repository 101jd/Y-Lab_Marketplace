package org.y_lab.application.model.MarketPlace;

import org.y_lab.application.exceptions.QtyLessThanZeroException;

/**
 * Domain class Item
 */
public class Item {
    private Product product;
    int qty;
    boolean inStock;

    /**
     * Base constructor
     * Validates qty (can be equal or more than 0)
     * @param product
     * @param qty
     * @throws QtyLessThanZeroException if validate qty fails
     */
    public Item(Product product, Integer qty) throws QtyLessThanZeroException {
        this.product = product;
        this.qty = validateQty(qty);
        this.inStock = isInStock();
    }

    public Item(){}

    public Product getProduct() {
        return product;
    }

    public Integer getQty() {
        return qty;
    }

    public boolean isInStock() {

        return qty > 0;
    }

    private int validateQty(int qty) throws QtyLessThanZeroException {
        if (qty < 0)
                throw new QtyLessThanZeroException();
        return qty;
    }

    /**
     * Decrements qty on 1
     */
    public void decrementQty(){
        --qty;
    }

    @Override
    public String toString() {
        return "Item{" +
                "product=" + product +
                ", qty=" + qty +
                ", inStock=" + inStock +
                '}';
    }
}
