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
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.exceptions.WrongUsernameOrPasswordException;
import org.y_lab.application.model.SignInData;
import org.y_lab.application.model.User;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.ServiceProvider;
import org.y_lab.application.service.interfaces.Service;

import javax.xml.bind.DataBindingException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

@WebServlet(name = "signInServlet", value = "/signin")
public class SignInServlet extends HttpServlet {
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
            SignInData data = mapper.readValue(req.getInputStream(), SignInData.class);
            User user = service.signIn(data.getUsername(), data.getPassword());
            resp.addCookie(new Cookie("userId", String.valueOf(user.getId())));

            PrintWriter writer = resp.getWriter();

            byte[] userBytes = mapper.writeValueAsBytes(user);
            writer.write(new String(userBytes));

            resp.getWriter().write("Hello, " + user.getUsername());
            resp.setStatus(HttpServletResponse.SC_OK);


        }catch (NoSuchElementException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No value present");
        }
        catch (DataBindingException | MismatchedInputException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Can't map to sign in data");
        } catch (UserNotFoundRuntimeException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        } catch (WrongUsernameOrPasswordException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Wrong username or password");
        }finally {
            resp.flushBuffer();
        }
    }
}
