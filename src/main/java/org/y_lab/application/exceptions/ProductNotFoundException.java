package org.y_lab.application.exceptions;

public class ProductNotFoundException extends Exception{
    public ProductNotFoundException(){
        super("Product not found");
    }
}
