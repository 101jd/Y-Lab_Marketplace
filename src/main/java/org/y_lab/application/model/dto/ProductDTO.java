package org.y_lab.application.model.dto;

public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Integer discount;

    public ProductDTO(Long id, String title, String description, Double price, Integer discount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.discount = discount;
    }

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
}
