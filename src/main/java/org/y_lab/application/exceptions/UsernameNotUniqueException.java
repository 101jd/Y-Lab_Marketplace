package org.y_lab.application.exceptions;

public class UsernameNotUniqueException extends Exception{
    public UsernameNotUniqueException() {
        super("Username is busy");
    }
}
