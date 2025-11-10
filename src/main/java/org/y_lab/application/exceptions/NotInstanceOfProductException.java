package org.y_lab.application.exceptions;

public class NotInstanceOfProductException extends RuntimeException{
    public NotInstanceOfProductException(){
        super("One or more elements in collection are not instances of Product");
    }
}
