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
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class EditItemServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Service service;

    @InjectMocks
    EditItemServlet servlet;

    @Test
    public void test() throws QtyLessThanZeroException, IOException, ProductNotFoundException, ServletException, UserNotFoundRuntimeException {
        Item modified = new Item(new Product(
                new ProductDTO(1L, "Product", "not test", 100.0, 25)), 2);
        AddressDTO addressDTO = new AddressDTO(UUID.randomUUID(), "City", "Street", 11, 2);
        UserDTO userDTO = new UserDTO(1L, "test", "1234",
                new Address(addressDTO), new Cart(), true);

        ServletInputStream inputStream = new DelegatingServletInputStream(
                new ObjectMapper().writeValueAsBytes(modified)
        );

        Cookie cookie = new Cookie("userId", String.valueOf(userDTO.getId()));

        Mockito.when(request.getInputStream()).thenReturn(inputStream);

        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        Mockito.when(service.getById(userDTO.getId())).thenReturn(new User(userDTO));

        Mockito.when(service.editProduct(modified.getProduct().getId(), modified)).thenReturn(modified);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        Mockito.when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        System.out.println("SW: " + stringWriter);

        Assertions.assertTrue(stringWriter.toString().contains(modified.getProduct().getTitle()));
    }
}
