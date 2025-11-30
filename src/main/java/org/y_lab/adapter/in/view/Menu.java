package org.y_lab.adapter.in.view;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.in.view.interfaces.View;
import org.y_lab.adapter.out.repository.ConnectionManager;
import org.y_lab.application.exceptions.ProductNotFoundException;
import org.y_lab.application.exceptions.QtyLessThanZeroException;
import org.y_lab.application.exceptions.UsernameNotUniqueException;
import org.y_lab.application.model.Address;
import org.y_lab.application.model.MarketPlace.Item;
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
                        Double maxPrice = tryParseDouble(string);
                        if (maxPrice != null){
                            notNumber = false;
                            view.findProductsByPrice(maxPrice).stream().forEach(System.out::println);
                        }
                    }
                    break;
                }

                case "add" : {
                    if (user != null) {
                        System.out.println("Enter product id");
                        String id = scanner.nextLine();
                        Long productId = tryParseLong(id);
                        if (productId != null) {
                            try {
                                System.out.println(view.addProductToCart(productId, this.user));
                            } catch (ProductNotFoundException e) {
                                System.out.println("Product not found");
                            }
                        } else System.out.println("Wrong id");
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
                        System.out.println("Enter id of editing Product:");
                        String inp = scanner.nextLine();
                        Long id = tryParseLong(inp);
                        if (id != null)
                            try {
                                ProductDTO product = this.editProduct(scanner, id);
                                Integer qty = this.getQty(scanner);
                                Item item = new Item(new Product(product), qty);
                                System.out.println(view.editItem(this.user, id, item));
                            } catch (QtyLessThanZeroException e) {
                                System.out.println(e.getMessage());
                            } catch (ProductNotFoundException e) {
                                System.out.println("Product not found");
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
        boolean b = false;
        String s = scanner.nextLine();
        Integer qty = tryParseInt(s);
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
            house = tryParseInt(houseNumber);
            if (house != null){
                flag = false;
            }
        }
        flag = true;
        while (flag){
            System.out.println("Enter apartment");
            String apart = scanner.nextLine();

            apartment = tryParseInt(apart);
            if (apartment != null){
                flag = false;
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
        price = tryParseDouble(in);

        System.out.println("Enter discount");
        in = scanner.nextLine();
        discount = tryParseInt(in);

        return new ProductDTO(id, title, descrption, price, discount);
    }


    private Integer tryParseInt(String s){
        try {
            return Integer.parseInt(s);

        }catch (NumberFormatException e){
            return null;
        }
    }

    private Double tryParseDouble(String s){
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException r){
            return null;
        }
    }

    private Long tryParseLong(String s){
        try {
            return Long.parseLong(s);

        }catch (NumberFormatException e){
            return null;
        }
    }
}
