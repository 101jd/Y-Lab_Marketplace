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
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.exceptions.WrongUsernameOrPasswordException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.SignInData;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.application.service.interfaces.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SignInServletTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Service service;

    @InjectMocks
    SignInServlet servlet;

    @Test
    public void test() throws IOException, UserNotFoundRuntimeException, ServletException, WrongUsernameOrPasswordException {
        AddressDTO addressDTO = new AddressDTO(UUID.randomUUID(), "City", "Street", 11, 2);
        UserDTO userDTO = new UserDTO(1L, "test", "1234",
                new Address(addressDTO), new Cart(), true);

        User user = new User(userDTO);

        SignInData signInData = new SignInData("test", "1234");

        ServletInputStream inputStream = new DelegatingServletInputStream(
                new ObjectMapper().writeValueAsBytes(signInData)
        );

        Mockito.when(request.getInputStream()).thenReturn(inputStream);

        Mockito.when(service.signIn(signInData.getUsername(), signInData.getPassword())).thenReturn(user);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        Mockito.when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains(user.getUsername()));
    }
}
