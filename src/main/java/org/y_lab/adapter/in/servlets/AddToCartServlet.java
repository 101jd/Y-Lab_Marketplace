package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.y_lab.application.annotations.Audition;
import org.y_lab.application.annotations.ToLog;
import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.User;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.ServiceProvider;
import org.y_lab.application.service.interfaces.Service;

import javax.xml.bind.DataBindingException;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AddToCartServlet", value = "/tocart")
public class AddToCartServlet extends HttpServlet {
    private Service service;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.service = ServiceProvider.getService();
    }

    @ToLog
    @Audition(message = "added product to cart")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setBufferSize(512);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Long id = mapper.readValue(req.getInputStream(), Long.class);

            String userId = req.getCookies()[0].getValue();
            User user = service.getById(Long.parseLong(userId));

            Item item = service.addProductToCart(id, user);

            resp.getWriter().write(mapper.writeValueAsString(item));
            resp.setStatus(HttpServletResponse.SC_OK);
            service.saveCart(user);
        }catch (DataBindingException | MismatchedInputException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong data");
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product not found");
        } catch (UserNotFoundRuntimeException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Please, register or sign in");
        } catch (ProductNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product not found");
        } finally {
            resp.flushBuffer();
        }
    }
}
