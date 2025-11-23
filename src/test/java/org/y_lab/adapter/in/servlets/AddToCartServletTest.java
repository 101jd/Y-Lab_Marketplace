package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.y_lab.adapter.in.servlets.support.DelegatingServletInputStream;
import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.application.service.interfaces.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AddToCartServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Service service;

    @InjectMocks
    AddToCartServlet servlet;

    @Test
    public void test() throws SQLException, IOException, QtyLessThanZeroException, UserNotFoundRuntimeException, ServletException, ProductNotFoundException {

        Item item = new Item(new Product(new ProductDTO(1L, "TEST", "t", 100.0, 25)), 2);
        AddressDTO addressDTO = new AddressDTO(UUID.randomUUID(), "City", "Street", 11, 2);
        UserDTO userDTO = new UserDTO(1L, "test", "1234",
                new Address(addressDTO), new Cart(), true);
        User user = new User(userDTO);


        ServletInputStream servletInputStream = new DelegatingServletInputStream(
                new ObjectMapper().writeValueAsBytes("1")
        );
        Mockito.when(request.getInputStream()).thenReturn(servletInputStream);
        Mockito.when(request.getCookies()).thenReturn(
                new Cookie[]{new Cookie("userId", String.valueOf(user.getId()))});
        Mockito.when(service.getById(1L)).thenReturn(user);
        Mockito.when(service.addProductToCart(1L, user)).thenReturn(item);
        Mockito.when(service.saveCart(user)).thenReturn(UUID.randomUUID());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);


        Mockito.when(response.getWriter()).thenReturn(writer);


        servlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains(item.getProduct().getTitle()));
    }
}
