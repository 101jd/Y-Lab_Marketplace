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
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.dto.ProductDTO;
import org.y_lab.application.service.interfaces.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Predicate;

@ExtendWith(MockitoExtension.class)
public class FilterByPriceServletTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Service service;

    @InjectMocks
    FilterByPriceServlet servlet;

    @Test
    public void test() throws QtyLessThanZeroException, IOException, ServletException {
        Item item = new Item(new Product(
                new ProductDTO(1L, "TEST", "test item", 100.0, 25)), 2);

        ServletInputStream inputStream = new DelegatingServletInputStream(new ObjectMapper().writeValueAsBytes("100"));

        Mockito.when(request.getInputStream()).thenReturn(inputStream);

        Mockito.doReturn(List.of(item)).when(service).filter((Predicate<? super Item>) Mockito.any());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        Mockito.when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        Assertions.assertTrue(stringWriter.toString().contains(item.getProduct().getTitle()));
    }
}
