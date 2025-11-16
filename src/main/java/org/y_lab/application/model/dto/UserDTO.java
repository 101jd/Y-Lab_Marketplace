package org.y_lab.application.model.dto;

import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;

import java.util.UUID;

public class UserDTO {

    private UUID id;
    private String username;
    private String password;
    private Address address;
    private Cart cart;
    private boolean admin;

    public UserDTO(UUID id, String username, String password, Address address, Cart cart, boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.cart = cart;
        this.admin = admin;
    }

    public UUID getId() {
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

    public boolean isAdmin() {
        return admin;
    }
}
