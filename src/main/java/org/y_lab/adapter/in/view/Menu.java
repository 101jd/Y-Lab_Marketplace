package org.y_lab.adapter.in.view;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.in.view.interfaces.View;
import org.y_lab.adapter.out.repository.ConnectionManager;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.MarketPlace.Product;
import org.y_lab.application.model.User;
import org.y_lab.application.model.dto.ProductDTO;

import java.sql.SQLException;
import java.util.Scanner;


public class Menu {
    View view;
    Scanner scanner;
    User user;

    public Menu(View view, Scanner scanner) {
        this.view = view;
        this.scanner = scanner;
        this.user = null;
    }

    public void menu(){
        boolean flag = true;
        System.out.println("""
                SELECT OPTION:
                r - register;
                si - sign in if you are registered;
                
                all - view all products in MP
                byTitle - search products by title
                byPrice - filter products by max price
                add - add product to cart
                
                if you are admin:
                ins - add new item to platform
                edit - edit item
                
                q - to quit
                """);

        while (flag){
            String input = scanner.nextLine();

            switch (input.toLowerCase()){
                case "r" : {
                    User u = this.register(scanner);
                    System.out.println(u);
                    try {
                        this.user = view.register(u);
                    } catch (UsernameNotUniqueException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "si" : {
                    System.out.println("Enter your username");
                    String username = scanner.nextLine();
                    System.out.println("Enter your password");
                    String password = scanner.nextLine();
                    this.user = view.signIn(username, password);
                    break;
                }

                case "all" : {
                    view.findAllProducts().stream().forEach(System.out::println);
                    break;
                }

                case "bytitle" : {
                    System.out.println("Enter keyword");
                    String keyword = scanner.nextLine();
                    view.findProductsByTitle(keyword).stream().forEach(System.out::println);
                    break;
                }

                case "byprice" : {
                    System.out.println("Enter max price");
                    boolean notNumber = true;
                    while (notNumber){
                        String string = scanner.nextLine();
                        if (tryParseDouble(string)){
                            notNumber = false;
                            double maxPrice = Double.parseDouble(string);
                            view.findProductsByPrice(maxPrice).stream().forEach(System.out::println);
                        }
                    }
                    break;
                }

                case "add" : {
                    if (user != null) {
                        System.out.println("Enter product id");
                        String uuid = scanner.nextLine();
                        if (tryParseLong(uuid)) {
                            Long productId = Long.valueOf(uuid);
                            System.out.println(view.addProductToCart(productId, this.user));
                        }
                    } else System.out.println("Please, register or sign in");
                 break;
                }

                case "ins" : {
                    if (this.user.isAdmin()){
                        Product product = this.createProduct(scanner);
                        Integer qty = this.getQty(scanner);
                        if (qty != null) {
                            try {
                                view.addItemToPlatform(this.user, product, qty);
                            } catch (QtyLessThanZeroException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } else System.out.println("You are not an admin!");
                    break;
                }

                case "edit" : {
                    if (this.user.isAdmin()){
                        System.out.println("Enter uuid of editing Product:");
                        String uuid = scanner.nextLine();
                        Long id = null;
                        if (tryParseLong(uuid))
                            id = Long.parseLong(uuid);
                        if (id != null)
                            try {
                                ProductDTO product = this.editProduct(scanner, id);
                                Integer qty = this.getQty(scanner);
                                System.out.println(view.editItem(this.user, id, product, qty));
                            } catch (QtyLessThanZeroException e) {
                                System.out.println(e.getMessage());
                            }
                    } else System.out.println("You are not an admin!");
                    break;
                }

                case "q" : {
                    flag = false;
                    if (this.user != null)
                        view.saveCart(this.user);
                    scanner.close();
                    try {
                        ConnectionManager.getInstance().close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (LiquibaseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                default:
                    System.out.println("Unknown command");
            }

        }

    }

    private Product createProduct(Scanner scanner) {
        ProductDTO product = null;
        boolean valid = false;
        while (!valid){
            System.out.println("All fields must be filled");
            product = editProduct(scanner, null);
            if (product.getTitle() != null &&
            product.getDescription() != null
            && product.getPrice() != null &&
            product.getDiscount() != null)
                valid = true;
        }
        return new Product(product);
    }

    private Integer getQty(Scanner scanner) {
        System.out.println("Enter qty");
        Integer qty = null;
        boolean b = false;
        String s = scanner.nextLine();
        b = tryParseInt(s);
        if (b)
            qty = Integer.parseInt(s);
        return qty;
    }

    private User register(Scanner scanner) {
        System.out.println("Enter username");
        String username = scanner.nextLine();
        System.out.println("Enter password");
        String password = scanner.nextLine();
        System.out.println("Enter city");
        String city = scanner.nextLine();
        System.out.println("Enter street");
        String street = scanner.nextLine();
        Integer house = null;
        Integer apartment = null;
        boolean admin = false;
        boolean flag = true;
        while (flag){
            System.out.println("Enter house number");
            String houseNumber = scanner.nextLine();
            if (tryParseInt(houseNumber)){
                flag = false;
                house = Integer.parseInt(houseNumber);
            }
        }
        flag = true;
        while (flag){
            System.out.println("Enter apartment");
            String apart = scanner.nextLine();
            if (tryParseInt(apart)){
                flag = false;
                apartment = Integer.parseInt(apart);
            }
        }
        System.out.println("Are you admin?");
        if (scanner.nextLine().toLowerCase().equals("y"))
            admin = true;

        User u = null;
        if (house != null && apartment != null)
                u = new User(username, password, new Address(city, street, house, apartment), admin);
        if (u == null)
            throw new RuntimeException("User wasn't created");
        return u;

    }

    private ProductDTO editProduct(Scanner scanner, Long id){
        System.out.println("Enter product title");
        String title = scanner.nextLine();
        if (title.length() == 0)
            title = null;
        System.out.println("Enter product description");
        String descrption = scanner.nextLine();
        if (descrption.length() == 0)
            descrption = null;

        Double price = null;
        Integer discount = null;

        System.out.println("Enter product price");
        String in = scanner.nextLine();
        if (tryParseDouble(in)){
            price = Double.parseDouble(in);
        }

        System.out.println("Enter discount");
        in = scanner.nextLine();
        if (tryParseInt(in)){
            discount = Integer.parseInt(in);
        }

        return new ProductDTO(id, title, descrption, price, discount);
    }


    private boolean tryParseInt(String s){
        try {
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private boolean tryParseDouble(String s){
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException r){
            return false;
        }
    }

    private boolean tryParseLong(String s){
        try {
            Long.parseLong(s);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}
