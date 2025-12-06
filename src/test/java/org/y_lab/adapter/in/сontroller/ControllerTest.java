package org.y_lab.adapter.in.сontroller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.y_lab.adapter.in.controller.Controller;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.SignInData;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ControllerTest {

    private MockMvc mockMvc;
    private Controller controller;
    private Service service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User mockUser;
    private Item mockItem;

    @BeforeEach
    void setup() throws QtyLessThanZeroException {
        service = Mockito.mock(Service.class);
        controller = new Controller(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();


        mockUser = new User(new UserDTO(1L, "testUser", "pass",
                new Address(new AddressDTO(UUID.randomUUID(), "city", "street", 11, 2)),
                new Cart(), true));
        mockItem = new Item(new Product(new ProductDTO(1L, "testProduct", "desc", 100.0, 10)), 3);
    }

    @Test
    void getAllProducts() throws Exception {
        Mockito.when(service.getAllProducts()).thenReturn(java.util.List.of(mockItem));

        mockMvc.perform(get("/allProducts"))
                .andExpect(status().isOk());
    }

    @Test
    void addToCart() throws Exception {
        Mockito.when(service.getById(1L)).thenReturn(mockUser);
        Mockito.when(service.addProductToCart(mockItem.getProduct().getId(), mockUser)).thenReturn(mockItem);

        mockMvc.perform(post("/addToCart")
                        .cookie(new Cookie("userId", "1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockItem.getProduct().getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void registerTest() throws Exception {
        User newUser = new User("Test", "123", new Address("city", "street", 1, 2), true);
        Mockito.when(service.register(newUser)).thenReturn(newUser);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void addToPlatformTest() throws Exception {
        Mockito.when(service.addItem(mockItem)).thenReturn(mockItem.getProduct().getId());
        Mockito.when(service.getById(1l)).thenReturn(mockUser);

        mockMvc.perform(post("/addToPlatform").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockItem))
                .cookie(new Cookie("userId", "1"))).andExpect(status().isOk());
    }

    @Test
    public void signInTest() throws Exception {
        Mockito.when(service.getById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(service.signIn(mockUser.getUsername(), "pass")).thenReturn(mockUser);

        mockMvc.perform(get("/signIn").contentType(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(new SignInData(mockUser.getUsername(), "pass"))))
                .andExpect(status().isOk());
    }
}
