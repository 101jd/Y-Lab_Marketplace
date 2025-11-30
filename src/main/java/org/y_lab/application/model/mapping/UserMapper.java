package org.y_lab.application.model.mapping;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.CartDTO;
import org.y_lab.application.model.dto.UserDTO;

@Mapper
public interface UserMapper {
    User toEntity(UserDTO userDTO);
    Address toEntity(AddressDTO addressDTO);
    Cart toEntity(CartDTO cartDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserDTO userDTO, @MappingTarget User user);
}
