package org.y_lab.adapter.in.view;

import org.y_lab.adapter.in.view.interfaces.View;
import org.y_lab.application.exceptions.*;
import org.y_lab.application.model.AuditionEntity;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.service.AuditionService;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.ServiceProvider;
import org.y_lab.application.service.interfaces.AuditionHandler;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ConsoleView implements View {
    private Service service;
    private AuditionHandler auditionHandler;
    private Logger logger;

    public ConsoleView() {
            this.service = ServiceProvider.getService();
            this.auditionHandler = AuditionService.getInstance();
            this.logger = Logger.getLogger("ConsoleLogs");
    }

    @Override
    public User register(User user) throws UsernameNotUniqueException {
        try {
            User u = service.register(user);
            AuditionEntity entity = new AuditionEntity(u.getId(), "registered");
            return u;
        }catch (UsernameNotUniqueException e){
            System.out.println("Register failed");
        }
        return null;
    }

    @Override
    public User signIn(String username, String password) {
        try {
            User u = service.signIn(username, password);
            auditionHandler.save(new AuditionEntity(u.getId(), "signed in"));
        } catch (UserNotFoundRuntimeException e) {

        } catch (WrongUsernameOrPasswordException e) {
            logger.warning(username + " entered wrong password");
        } catch (SQLException e) {
            logger.warning("SQL exception " + e.getMessage());
        }
        return null;
    }

    @Override
    public Item addProductToCart(Long id, User user) throws ProductNotFoundException {
        try {
            Item item = service.findById(id);
            auditionHandler.save(new AuditionEntity(user.getId(), "added to cart " + id));
            return service.addProductToCart(item, user);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } catch (ProductNotFoundException e) {
            throw e;
        }
    }

    @Override
    public List<Item> findAllProducts() {
        return service.getAllProducts();
    }

    @Override
    public List<Item> findProductsByTitle(String title) {
        return service.filter(item -> item.getProduct().getTitle().toLowerCase().contains(title) ||
                item.getProduct().getDescription().toLowerCase().contains(title));
    }

    @Override
    public List<Item> findProductsByPrice(double maxPrice) {
        return service.filter(item -> item.getProduct().getTotalPrice() <= maxPrice);
    }

    @Override
    public Long addItemToPlatform(User user, Product product, int qty) throws QtyLessThanZeroException {
        try {
            Long id = service.addItem(new Item(product, qty));
            auditionHandler.save(new AuditionEntity(user.getId(), "added to platform + " + product.getId() +
                    " with qty " + qty));
            return id;
        } catch (QtyLessThanZeroException e) {
            throw e;
        } catch (SQLException e) {
            logger.warning("SQL exception " + e.getMessage());
        }
        return null;
    }

    @Override
    public Item editItem(User user, Long itemId, Item item) throws QtyLessThanZeroException, ProductNotFoundException {
        Item baseItem = null;
        try {
            baseItem = service.findById(itemId);
        } catch (ProductNotFoundException e) {
            System.out.println("Product not found");
        }
        Product baseProduct = baseItem.getProduct();
        Product newProduct = item.getProduct();

        String title = newProduct.getTitle() == null ? baseProduct.getTitle() :
                newProduct.getTitle();
        String description = newProduct.getDescription() == null ? baseProduct.getTitle() :
                newProduct.getTitle();
        Double price = newProduct.getPrice() == null ? baseProduct.getPrice() :
                newProduct.getPrice();
        Integer discount = newProduct.getDiscount() == null ? baseProduct.getDiscount() :
                newProduct.getDiscount();

        Integer qty = item.getQty();
        qty = qty == null ? baseItem.getQty() : qty;
        Item newItem = new Item(new Product(
                        new ProductDTO(itemId, title, description, price, discount)), qty);

        try {
            auditionHandler.save(new AuditionEntity(user.getId(), "edited " + newItem.getProduct().getId()));
        } catch (SQLException e) {
            logger.warning("SQL Exception " + e.getMessage());
        }
        try {
            return service.editProduct(itemId, newItem);
        } catch (ProductNotFoundException e) {
            throw e;
        }
    }

    @Override
    public void saveCart(User user) {
        try {
            service.saveCart(user);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
}
