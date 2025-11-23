package org.y_lab.application.model.mapping;

import org.mapstruct.*;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.model.dto.ItemDTO;

@Mapper
public interface ItemMapper {
    Item toEntity(ItemDTO itemDTO);
    Product toEntity(ProductDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ItemDTO itemDTO, @MappingTarget Item item);
}
