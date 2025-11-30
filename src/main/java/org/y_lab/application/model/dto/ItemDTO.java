package org.y_lab.application.model.dto;

import jakarta.validation.constraints.NotNull;

public class ItemDTO {
    @NotNull(message = "Item must contain product")
    ProductDTO productDTO;
    Integer qty;

    public ItemDTO(ProductDTO productDTO, Integer qty) {
        this.productDTO = productDTO;
        this.qty = qty;
    }

    public ItemDTO(){}

    public ProductDTO getProductDTO() {
        return productDTO;
    }
}
