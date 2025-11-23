package org.y_lab.application.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;

public class UserDTO {

    @JsonProperty(required = false)
    @Nullable
    private Long id;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull(message = "You must enter password")
    @NotEmpty(message = "Password can not be empty")
    private String password;
    @NotNull
    private Address address;
    @Nullable
    private Cart cart;
    @Nullable
    private Boolean admin;

    public UserDTO(Long id, String username, String password, Address address, Cart cart, boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.cart = cart;
        this.admin = admin;
    }

    public UserDTO(){
        this.cart = new Cart();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Address getAddress() {
        return address;
    }

    public Cart getCart() {
        return cart;
    }

    public Boolean isAdmin() {
        return admin;
    }
}
