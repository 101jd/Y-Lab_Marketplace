package org.y_lab.application.service.interfaces;

import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;

public interface Service extends MPService, UserService{

    void saveCart(User user);
}
