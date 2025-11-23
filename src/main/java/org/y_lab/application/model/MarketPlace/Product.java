package org.y_lab.application.model.MarketPlace;

import org.y_lab.application.exceptions.WrongDiscountException;
import org.y_lab.application.model.dto.ProductDTO;

import java.util.Objects;

/**
 * Domain class Product
 */
public class Product {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Integer discount;


    /**
     * Constructor from DTO
     * @param dto
     */
    public Product(ProductDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        setDiscount(dto.getDiscount());
    }

    /**
     * Base constructor
     * @param title - name of Product
     * @param description
     * @param price
     * @param discount - from 0% to 99%
     * @throws WrongDiscountException RuntimeException
     */
    public Product(String title, String description, Double price, Integer discount) throws WrongDiscountException {
        this.id = null;
        this.title = title;
        this.description = description;
        this.price = price;
        setDiscount(discount);
    }

    /**
     * Without discount constructor
     * @param title - name of Product
     * @param description
     * @param price
     * Discount automatically set to 0%
     */
    public Product(String title, String description, double price) {
        this(title, description, price, 0);
    }


    /**
     * Set discount in percentage
     * @param discount - greater or equals 0 less than 100
     * @throws WrongDiscountException RuntimeException
     */
    public void setDiscount(int discount) throws WrongDiscountException {
        if (discount > 99 || discount < 0)
                throw new WrongDiscountException();
        this.discount = discount;
    }

    //region getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getDiscount() {
        return discount;
    }

    /**
     * @return total price with discount applied
     */
    public double getTotalPrice(){
        return this.price - (this.price / 100 * this.discount);
    }

    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(price, product.price) == 0 && discount == product.discount && Objects.equals(id, product.id) && Objects.equals(title, product.title) && Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, price, discount);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", total=" + getTotalPrice() +
                '}';
    }
}
