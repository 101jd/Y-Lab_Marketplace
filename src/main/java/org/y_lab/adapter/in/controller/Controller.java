package org.y_lab.adapter.in.controller;

import com._jd.Audition;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._jd.ToLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.UserNotFoundRuntimeException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.exceptions.WrongUsernameOrPasswordException;
import org.y_lab.application.model.MarketPlace.Item;
import org.y_lab.application.model.SignInData;
import org.y_lab.application.model.User;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("/")
public class Controller {

    private Service service;


    @Autowired
    public Controller(Service service) {
        this.service = service;
    }


    /**
     *
     * @return List of all items
     */
    @ToLog
    @GetMapping("allProducts")
    public ResponseEntity<List<Item>> getAll(){
        return ResponseEntity.ok(service.getAllProducts());
    }

    @ToLog
    @GetMapping("productById")
    public ResponseEntity<Item> productById(@RequestBody Long id){
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ToLog
    @GetMapping("filterByKeyword")
    public ResponseEntity<List<Item>> byKeyword(@RequestBody String keyword){
        return ResponseEntity.ok(service.filter(item -> item.getProduct().getTitle().equals(keyword)
                || item.getProduct().getDescription().equals(keyword)));
    }

    @ToLog
    @GetMapping("filterByPrice")
    public ResponseEntity<List<Item>> byPrice(@RequestBody Double price){
        return ResponseEntity.ok(service.filter(item -> item.getProduct().getTotalPrice() <= price));
    }

    @ToLog
    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody User user, HttpServletResponse response){
        try {
            User u = service.register(user);

            response.addCookie(createCookie(u));

            return ResponseEntity.ok(u);
        } catch (UsernameNotUniqueException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ToLog
    @GetMapping("signIn")
    public ResponseEntity<User> signIn(@RequestBody SignInData data, HttpServletResponse response){
        try {
            User user = service.signIn(data.getUsername(), data.getPassword());
            response.addCookie(createCookie(user));
            return ResponseEntity.ok(user);
        } catch (UserNotFoundRuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (WrongUsernameOrPasswordException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @ToLog
    @Audition(message = "adding item to cart")
    @PostMapping("addToCart")
    public ResponseEntity<Item> addToCart(@RequestBody Long itemId, HttpServletRequest request){
        try {
            User user = service.getById(Long.parseLong(request.getCookies()[0].getValue()));
            return ResponseEntity.ok(service.addProductToCart(itemId, user));
        } catch (SQLException e) {
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserNotFoundRuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @ToLog
    @Audition(message = "adding item to platform")
    @PostMapping("addToPlatform")
    public ResponseEntity<Long> addToPlatform(@RequestBody Item item, HttpServletRequest request){
        try {
            User user = service.getById(Long.parseLong(request.getCookies()[0].getValue()));
            if (user.isAdmin())
                return ResponseEntity.ok(service.addItem(item));
            else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserNotFoundRuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Cookie createCookie(User user){
        Cookie cookie = new Cookie("userId", String.valueOf(user.getId()));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        return cookie;
    }
}
