package org.y_lab.application.service.interfaces;

import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.User;

import java.sql.SQLException;
import java.util.UUID;

public interface UserService {
    User register(User user) throws UsernameNotUniqueException;
    User signIn(String username, String password);
    Cart getCart(User user);

    Item addProductToCart(Item item, User user) throws SQLException;
    Item addProductToCart(UUID id, User user) throws SQLException;
}
