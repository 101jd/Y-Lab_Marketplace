package org.y_lab.adapter.in.сontroller;

import jakarta.servlet.http.Cookie;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.y_lab.adapter.in.controller.Controller;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.Cart;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.AddressDTO;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.interfaces.Service;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock
    private PlatformServiceImpl service;

    @InjectMocks
    private Controller controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void getAll() throws Exception {
        mockMvc.perform(get("/allProducts")).andExpect(status().isOk());
    }

    @Test
    public void testToCartIsBadRequest() throws Exception {
        Item item = new Item(new Product(
                new ProductDTO(1L, "test", "test", 100.0, 10)), 3);

        mockMvc.perform(post("/addToCart").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(item)
        )).andExpect(status().isBadRequest());
    }

    @Test
    public void testSuccessAddToCart() throws Exception {
        Item item = new Item(new Product(
                new ProductDTO(1L, "test", "test", 100.0, 10)), 3);
        User user = new User(new UserDTO(1L, "test", "test", new Address(
                new AddressDTO(UUID.randomUUID(), "w", "asd", 11, 2)),
                new Cart(), true));

        Mockito.when(service.getById(1L)).thenReturn(user);
        Mockito.when(service.addProductToCart(item.getProduct().getId(), user)).thenReturn(item);

        mockMvc.perform(post("/addToCart").contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("userId", "1"))
                        .content(objectMapper.writeValueAsString(item.getProduct().getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void registerTest() throws Exception {
        User user =
                new User("Test", "123",
                        new Address("city", "street", 1, 2), true);

        Mockito.when(service.register(user)).thenReturn(user);

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))).andExpect(status().isOk());
    }
}
