package org.y_lab.application.service.interfaces;

import org.y_lab.application.model.User;

import java.sql.SQLException;
import java.util.UUID;

@org.springframework.stereotype.Service
public interface Service extends MarketPlaceService, UserService{

    UUID saveCart(User user) throws SQLException;
}
