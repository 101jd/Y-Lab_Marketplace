package org.y_lab.application.exceptions;

public class UserNotFoundRuntimeException extends Exception{
    public UserNotFoundRuntimeException(String user) {
        super("User " + user + " not found");
    }
}
