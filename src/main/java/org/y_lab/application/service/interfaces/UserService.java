package org.y_lab.application.service.interfaces;

import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.exceptions.WrongUsernameOrPasswordException;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.User;

import java.sql.SQLException;

public interface UserService {
    User register(User user) throws UsernameNotUniqueException;
    User signIn(String username, String password) throws WrongUsernameOrPasswordException, UserNotFoundRuntimeException;
    Cart getCart(User user);

    Item addProductToCart(Item item, User user) throws SQLException;
    Item addProductToCart(Long id, User user) throws SQLException, ProductNotFoundException;
    User getById(Long id) throws UserNotFoundRuntimeException;
}
