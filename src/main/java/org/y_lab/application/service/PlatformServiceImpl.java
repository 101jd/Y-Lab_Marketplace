package org.y_lab.application.service;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.out.repository.AuditionRepository;
import org.y_lab.adapter.out.repository.CartRepository;
import org.y_lab.adapter.out.repository.MarketPlaceRepository;
import org.y_lab.adapter.out.repository.UserRepositoryImpl;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SaveRepository;
import org.y_lab.adapter.out.repository.interfaces.SimpleRepository;
import org.y_lab.application.exceptions.*;
import org.y_lab.application.model.AuditionEntity;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Platform;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class PlatformServiceImpl implements Service {

    private Repository<Long, User> userRepository;
    private SimpleRepository<UUID, Cart> cartSaveRepo;
    private Repository<Long, Item> mpRepository;
    private SaveRepository<Long, AuditionEntity> auditionRepository;
    private Platform platform;

    public PlatformServiceImpl() throws SQLException, LiquibaseException {
        this.userRepository = new UserRepositoryImpl();
        this.mpRepository = new MarketPlaceRepository();
        this.platform = new Platform();
        this.cartSaveRepo = new CartRepository();
        this.auditionRepository = new AuditionRepository();
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
            auditionRepository.save(new AuditionEntity(user.getId(), "Registered"));
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
            Long id = u.getId();
            auditionRepository.save(new AuditionEntity(id, "trying to login"));
            if (u.comparePasswords(password)){
                u.setCart(cartSaveRepo.getById(u.getId()));
                auditionRepository.save(new AuditionEntity(id, "successfully logged in"));
                return u;
            }
            throw new WrongUsernameOrPasswordException();
        } catch (SQLException e) {
            throw new UserNotFoundRuntimeException();
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
            auditionRepository.save(new AuditionEntity(user.getId(), "added item to cart " +
                    item.getProduct().getId()));
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
    public Item addProductToCart(Long id, User user) {
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
    public Long addItem(User user, Item item) {
        try {
            Long id = mpRepository.save(item);
            this.platform.addItem(item);
            auditionRepository.save(new AuditionEntity(user.getId(), "Added item to platform "
                    + item.getProduct().getId() + " with qty " + item.getQty()));
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
    public Item editProduct(User user, Long id, Item item) {
        try {
            auditionRepository.save(new AuditionEntity(user.getId(), "Edit product " + id));
            return mpRepository.update(id, item);
        } catch (Exception e){
            throw new ProductNotFoundException();
        }
    }

    /**
     * @param item to delete
     * @return deleted Item
     */
    @Override
    public boolean deleteProduct(User user, Item item) {
        try {
            auditionRepository.save(new AuditionEntity(user.getId(), "Deleted product " + item.getProduct().getId()));
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
    public Item findById(User user, Long productId) throws ProductNotFoundException {
        try {
            auditionRepository.save(new AuditionEntity(user.getId(), "Looking for " + productId));
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
     * @param id of modifying Product
     * @param discount new value
     * @return modified Item
     * @throws WrongDiscountException in Runtime
     */
    @Override
    public Item setDiscount(Long id, int discount) throws WrongDiscountException {
        try {
            Item item = mpRepository.getById(id);
            Product product = item.getProduct();
            product.setDiscount(discount);
            return mpRepository.update(id, item);
        } catch (WrongDiscountException e){
            throw e;
        } catch (SQLException e){
            throw new ProductNotFoundException();
        }
    }

    @Override
    public UUID saveCart(User user) throws SQLException {
        auditionRepository.save(new AuditionEntity(user.getId(), "Cart saved"));
        return cartSaveRepo.save(user.getCart());
    }
}
