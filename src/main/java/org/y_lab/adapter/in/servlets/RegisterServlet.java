package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.y_lab.application.annotations.ToLog;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.UserDTO;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.ServiceProvider;
import org.y_lab.application.service.interfaces.Service;

import javax.xml.bind.DataBindingException;
import java.io.IOException;

@WebServlet(name = "regServlet", value = "/registration")
public class RegisterServlet extends HttpServlet {
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
        resp.setBufferSize(1024);


        ObjectMapper mapper = new ObjectMapper();
        try {
            UserDTO user = mapper.readValue(req.getInputStream(), UserDTO.class);
            try {
                User respUser = service.register(new User(user));
                resp.addCookie(new Cookie("userId", String.valueOf(respUser.getId())));
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(mapper.writeValueAsString(user));
            } catch (UsernameNotUniqueException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }catch (DataBindingException | MismatchedInputException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong data");
        }catch (NullPointerException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        finally {
            resp.flushBuffer();
        }

    }
}
