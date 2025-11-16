package org.y_lab.adapter.in.view.interfaces;

import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface View {
    /**
     * Register new user
     * @param user to register
     * @return registered user
     * @throws UsernameNotUniqueException in runtime
     */
    User register(User user) throws UsernameNotUniqueException;

    /**
     * Check username and password
     * @param username to find in DB
     * @param password to compare
     * @return user exists and password is correct
     */
    User signIn(String username, String password);

    /**
     * Adds product to user's cart and decrease qty in product's item
     * @param id of product
     * @param user cart owner
     * @return modified Item
     */
    Item addProductToCart(Long id, User user);

    /**
     *
     * @return All products in DB
     */
    List<Item> findAllProducts();

    /**
     * Filter products by keyword
     * @param title keyword
     * @return Fetch of products
     */
    List<Item> findProductsByTitle(String title);

    /**
     * Filter products by max price
     * @param maxPrice
     * @return Products with price less than maxPrice
     */
    List<Item> findProductsByPrice(double maxPrice);

    /**
     * Adds Item to platform
     * @param product
     * @param qty
     * @return product id
     * @throws QtyLessThanZeroException
     */
    Long addItemToPlatform(User user, Product product, int qty) throws QtyLessThanZeroException;

    /**
     *
     * @param itemId of modifying product item
     * @param newProduct
     * @param qty new qty
     * @return Modified Item
     * @throws QtyLessThanZeroException
     */
    Item editItem(User user, Long itemId, ProductDTO newProduct, Integer qty) throws QtyLessThanZeroException;

    void saveCart(User user);
}
