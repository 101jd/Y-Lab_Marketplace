package org.y_lab.application.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ProductDTO {
    @Nullable
    private Long id;
    @NotNull
    @NotEmpty
    private String title;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    private Double price;
    @Nullable
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
