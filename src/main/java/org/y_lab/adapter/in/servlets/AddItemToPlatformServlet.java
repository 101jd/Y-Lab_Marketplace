package org.y_lab.adapter.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import org.y_lab.application.annotations.Audition;
import org.y_lab.application.annotations.ToLog;
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ItemDTO;
import org.y_lab.application.model.mapping.ItemMapper;
import org.y_lab.application.service.PlatformServiceImpl;
import org.y_lab.application.service.ServiceProvider;
import org.y_lab.application.service.interfaces.Service;

import javax.xml.bind.DataBindingException;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AddToPlatform", value = "/add")
public class AddItemToPlatformServlet extends HttpServlet {

    private Service service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.service = ServiceProvider.getService();
    }

    @ToLog
    @Audition(message = "added item to platform")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setBufferSize(1024);

        try {
            User user = service.getById(Long.parseLong(req.getCookies()[0].getValue()));

            if (user.isAdmin()) {
                ObjectMapper mapper = new ObjectMapper();
                ItemDTO itemDTO = mapper.readValue(req.getInputStream(), ItemDTO.class);

                ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

                Item item = itemMapper.toEntity(itemDTO);
                Long result = service.addItem(item);

                resp.getWriter().write(String.valueOf(result));

                resp.setStatus(HttpServletResponse.SC_OK);
            } else resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permissions to add items");
        }catch (DataBindingException | MismatchedInputException | SQLException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong data");
        } catch (UserNotFoundRuntimeException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Please, register or sign in");
        }finally {
            resp.flushBuffer();
        }
    }
}
