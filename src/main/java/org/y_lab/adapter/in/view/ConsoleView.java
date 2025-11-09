package org.y_lab.adapter.in.view;

import org.y_lab.adapter.in.view.interfaces.View;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Platform;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class ConsoleView implements View {
    private Service service;
    private User user;

    public ConsoleView() {
        try {
            this.service = new PlatformServiceImpl();
        } catch (SQLException e) {
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
    public Item addProductToCart(UUID uuid, User user) {
        try {
            Item item = service.findById(uuid);
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
    public UUID addItemToPlatform(Product product, int qty) throws QtyLessThanZeroException {
        try {
            return service.addItem(new Item(product, qty));
        } catch (QtyLessThanZeroException e) {
            throw e;
        }
    }

    @Override
    public Item editItem(UUID uuid, ProductDTO newProduct, Integer qty) throws QtyLessThanZeroException {
        Item baseItem = service.findById(uuid);
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
                        new ProductDTO(uuid, title, description, price, discount)), qty);
        return service.editProduct(uuid, newItem);
    }

    @Override
    public void saveCart(User user) {
        service.saveCart(user);
    }
}
