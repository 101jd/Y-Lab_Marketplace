package org.y_lab.adapter.in.view;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.in.view.interfaces.View;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.List;

public class ConsoleView implements View {
    private Service service;
    private User user;

    public ConsoleView() {
        try {
            this.service = new PlatformServiceImpl();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User register(User user) throws UsernameNotUniqueException {
        return service.register(user);
    }

    @Override
    public User signIn(String username, String password) {
        return service.signIn(username, password);
    }

    @Override
    public Item addProductToCart(Long id, User user) {
        try {
            Item item = service.findById(user, id);
            return service.addProductToCart(item, user);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
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
            return service.addItem(user, new Item(product, qty));
        } catch (QtyLessThanZeroException e) {
            throw e;
        }
    }

    @Override
    public Item editItem(User user, Long itemId, ProductDTO newProduct, Integer qty) throws QtyLessThanZeroException {
        Item baseItem = service.findById(user, itemId);
        Product baseProduct = baseItem.getProduct();

        String title = newProduct.getTitle() == null ? baseProduct.getTitle() :
                newProduct.getTitle();
        String description = newProduct.getDescription() == null ? baseProduct.getTitle() :
                newProduct.getTitle();
        Double price = newProduct.getPrice() == null ? baseProduct.getPrice() :
                newProduct.getPrice();
        Integer discount = newProduct.getDiscount() == null ? baseProduct.getDiscount() :
                newProduct.getDiscount();

        qty = qty == null ? baseItem.getQty() : qty;
        Item newItem = new Item(new Product(
                        new ProductDTO(itemId, title, description, price, discount)), qty);
        return service.editProduct(user, itemId, newItem);
    }

    @Override
    public void saveCart(User user) {
        try {
            service.saveCart(user);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }
}
