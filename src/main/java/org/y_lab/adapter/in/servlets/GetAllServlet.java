package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.y_lab.application.annotations.ToLog;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.ServiceProvider;
import org.y_lab.application.service.interfaces.Service;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetAllServlet", value = "/getall")
public class GetAllServlet extends HttpServlet {
    private Service service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.service = ServiceProvider.getService();
    }

    @ToLog
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Item> items = service.getAllProducts();

        resp.setContentType("application/json");
        resp.setBufferSize(2048);

        ObjectMapper mapper = new ObjectMapper();

        resp.getWriter().write(mapper.writeValueAsString(items));

        resp.setStatus(HttpServletResponse.SC_OK);

        resp.flushBuffer();
    }
}
