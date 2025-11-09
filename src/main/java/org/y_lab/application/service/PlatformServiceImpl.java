package org.y_lab.application.service;

import org.postgresql.util.MD5Digest;
import org.y_lab.adapter.out.repository.CartRepo;
import org.y_lab.adapter.out.repository.MarketPlaceRepository;
import org.y_lab.adapter.out.repository.UserRepositoryImpl;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SaveRepo;
import org.y_lab.application.exceptions.*;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Platform;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.service.interfaces.Service;


import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class PlatformServiceImpl implements Service {

    Repository<User> userRepository;
    SaveRepo<Cart> cartSaveRepo;
    private Repository<Item> mpRepository;
    private Platform platform;

    public PlatformServiceImpl() throws SQLException {
        userRepository = UserRepositoryImpl.getInstance();
        mpRepository = new MarketPlaceRepository();
        platform = new Platform();
        cartSaveRepo = new CartRepo();
    }


    /**
     * Username must be unique
     * @param user new user
     * @return registered user
     * @throws UsernameNotUniqueException
     */
    @Override
    public User register(User user) throws UsernameNotUniqueException {
        try {
            userRepository.save(user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UsernameNotUniqueException();
        }

    }

    /**
     * Sign In
     * Checks if user exists and passwords are equal.
     * @param username to find in BD
     * @param password to compare
     * @return username exists & passwords are equal
     */
    @Override
    public User signIn(String username, String password) {
        try {
            User u = userRepository.getAll().stream().filter(user -> user.getUsername().equals(username))
                    .findFirst().get();
            UUID id = u.getId();
            if (u.comparePasswords(password)){
                u.setCart(cartSaveRepo.getById(u.getId()));
                return u;
            }
            throw new WrongUsernameOrPasswordException();
        } catch (SQLException e) {
            throw new UserNotFoundException();
        }
    }

    /**
     *
     * @param user
     * @return cart of this user
     */
    @Override
    public Cart getCart(User user) {
        return user.getCart();
    }

    /**
     * Adds product to cart and automatically decrease qty in Product's Item
     * @param item to get product
     * @param user cart owner
     * @return modified Item
     */
    @Override
    public Item addProductToCart(Item item, User user) {
        Item i = platform.releaseProductToCart(item, user.getCart());
        try {
            mpRepository.update(i.getProduct().getId(), i);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
        return i;
    }

    /**
     * Adds product to cart and automatically decrease qty in Product's Item
     * @param id product id
     * @param user cart owner
     * @return modified Item
     * @throws ProductNotFoundException if product(item) not found
     */
    @Override
    public Item addProductToCart(UUID id, User user) {
        try {
            Item item = mpRepository.getById(id);
            return addProductToCart(item, user);
        } catch (SQLException e) {
            throw new ProductNotFoundException();
        }


    }

    /**
     * Saves Item in Repository
     * @param item to save
     * @return id of saved product
     * @throws SQLRuntimeException if something wrong with connection
     */
    @Override
    public UUID addItem(Item item) {
        try {
            UUID id = mpRepository.save(item);
            this.platform.addItem(item);
            return id;
        } catch (SQLException e){
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    /**
     * Modify Item (for modify qty or Product)
     * @param id of modifying item's Product
     * @param item modified
     * @return modified Product
     */
    @Override
    public Item editProduct(UUID id, Item item) {
        try {
            return mpRepository.update(id, item);
        } catch (Exception e){
            throw new ProductNotFoundException();
        }
    }

    /**
     *
     * @param item to delete
     * @return deleted Item
     */
    @Override
    public Item deleteProduct(Item item) {
        try {
            return mpRepository.delete(item);
        } catch (Exception e){
            throw new ProductNotFoundException();
        }
    }

    /**
     *
     * @return List of All products in DataBase (can be empty)
     *  @throws SQLRuntimeException if something wrong with connection or ResultSet
     */
    @Override
    public List<Item> getAllProducts() {
        try {
            return mpRepository.getAll();
        }catch (SQLException e){
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    /**
     * @Nullable
     * @param productId
     * @return Item found by id if exists
     * @throws ProductNotFoundException in Runtime
     */
    @Override
    public Item findById(UUID productId) throws ProductNotFoundException {
        try {
            return mpRepository.getById(productId);
        } catch (SQLException e){
            throw new ProductNotFoundException();
        }
    }

    /**
     *
     * @param predicate is filter for selection
     * @return sample list by filter
     * @throws SQLRuntimeException in Runtime
     */
    @Override
    public List<Item> filter(Predicate<? super Item> predicate) {
        try {
            return mpRepository.getAll().stream().filter(predicate).toList();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    /**
     * Set and update discount to Product
     * @param uuid of modifying Product
     * @param discount new value
     * @return modified Item
     * @throws WrongDiscountException in Runtime
     */
    @Override
    public Item setDiscount(UUID uuid, int discount) throws WrongDiscountException {
        try {
            Item item = mpRepository.getById(uuid);
            Product product = item.getProduct();
            product.setDiscount(discount);
            return mpRepository.update(uuid, item);
        } catch (WrongDiscountException e){
            throw e;
        } catch (SQLException e){
            throw new ProductNotFoundException();
        }
    }

    @Override
    public void saveCart(User user) {

    }
}
