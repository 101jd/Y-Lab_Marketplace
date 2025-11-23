package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
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
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.application.service.interfaces.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RegisterServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Service service;

    @InjectMocks
    RegisterServlet servlet;

    @Test
    public void test() throws IOException, UsernameNotUniqueException, ServletException {

        ObjectMapper mapper = new ObjectMapper();

        AddressDTO addressDTO = new AddressDTO(UUID.randomUUID(), "City", "Street", 11, 2);
        UserDTO userDTO = new UserDTO(1L, "test", "1234",
                new Address(addressDTO), new Cart(), true);

        User user = new User(userDTO);


        ServletInputStream inputStream =
                new DelegatingServletInputStream(mapper.writeValueAsBytes(userDTO));


        Mockito.when(request.getInputStream()).thenReturn(inputStream);
        Mockito.when(service.register(user)).thenReturn(user);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        Mockito.when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains(user.getUsername()));
    }
}
