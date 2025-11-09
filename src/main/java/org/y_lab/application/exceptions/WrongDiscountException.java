package org.y_lab.application.exceptions;

public class WrongDiscountException extends RuntimeException{
    public WrongDiscountException(){
        super("The discount value can't be greater than 99% or less than 0% ");
    }
}
