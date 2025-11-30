package org.y_lab.application.exceptions;

public class WrongUsernameOrPasswordException extends Exception{
    public WrongUsernameOrPasswordException() {
        super("Wrong user");
    }
}
