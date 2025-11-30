package org.y_lab.adapter.in.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.service.interfaces.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GetAllServletTest {
    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse resp;

    @Mock
    Service service;

    @InjectMocks
    GetAllServlet servlet;

    @Test
    public void test() throws IOException, QtyLessThanZeroException, ServletException {


        Item item = new Item(new Product("TEST", "t", 100.0, 25), 2);
        Mockito.when(service.getAllProducts()).thenReturn(List.of(item));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        Mockito.when(resp.getWriter()).thenReturn(writer);

        servlet.doGet(req, resp);
        Assertions.assertTrue(stringWriter.toString().contains(item.getProduct().getTitle()));


    }
}
