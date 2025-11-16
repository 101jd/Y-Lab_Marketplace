package org.y_lab.application.exceptions;

public class UserNotFoundRuntimeException extends RuntimeException{
    public UserNotFoundRuntimeException() {
        super("User not found");
    }
}
