package org.y_lab.application.model;

import org.postgresql.util.MD5Digest;
import org.y_lab.application.model.dto.UserDTO;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private byte[] password;
    private Address address;
    private Cart cart;
    private Boolean admin;

    /**
     * Constructor from DTO object
     * @param dto to copy fields
     */
    public User(UserDTO dto){
        if (dto.getId() != null)
            this.id = dto.getId();
        else this.id = 0L;
        this.username = dto.getUsername();
        this.password = encodePassword(this.username, dto.getPassword());
        this.address = dto.getAddress();
        this.cart = dto.getCart() == null ? new Cart() : dto.getCart();
        this.admin = dto.isAdmin() == null ? false : dto.isAdmin();
    }

    public User(){

    }

    /**
     * Base constructor
     * Id autoincrements in DB
     * @param username
     * @param password hashes with MD5
     * @param address
     * @param admin
     */
    public User(String username, String password, Address address, boolean admin) {
        this.id = null;
        this.username = username;
        this.password = encodePassword(this.username, password);
        this.address = address;
        this.cart = new Cart();
        this.admin = admin;
    }

    private byte[] encodePassword(String username, String password){
        if (password == null)
            return new byte[0];
        return MD5Digest.encode(username.getBytes(StandardCharsets.UTF_8),
                password.getBytes(StandardCharsets.UTF_8),
                "salt".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Compares password with this user's password
     * @param cPassword to compare
     * @return
     */
    public boolean comparePasswords(String cPassword){
        return new String(encodePassword(this.username, cPassword)).equals(new String(this.password));
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password=" + Arrays.toString(password) +
                ", address=" + address +
                ", cart=" + cart +
                ", admin=" + admin +
                '}';
    }

    public void setCart(Cart cart) {
        if (cart != null) {
            this.cart = cart;
        }
    }

}
