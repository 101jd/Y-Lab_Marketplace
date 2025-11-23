package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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

import javax.xml.bind.DataBindingException;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ByKeyword", value = "/keyword")
public class FilterByKeywordServlet extends HttpServlet {
    private Service service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.service = ServiceProvider.getService();
    }

    @ToLog
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setBufferSize(2048);

        try {
            ObjectMapper mapper = new ObjectMapper();

            String keyword = mapper.readValue(req.getInputStream(), String.class);
            List<Item> items = service.filter(item -> item.getProduct().getTitle().contains(keyword) ||
                    item.getProduct().getDescription().contains(keyword));

            resp.getWriter().write(mapper.writeValueAsString(items));

            resp.setStatus(HttpServletResponse.SC_OK);

        }catch (DataBindingException | MismatchedInputException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong data");
        }finally {
            resp.flushBuffer();
        }

    }
}
