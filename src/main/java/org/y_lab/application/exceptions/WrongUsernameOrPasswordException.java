package org.y_lab.application.exceptions;

public class WrongUsernameOrPasswordException extends RuntimeException{
    public WrongUsernameOrPasswordException() {
        super("Wrong user");
    }
}
