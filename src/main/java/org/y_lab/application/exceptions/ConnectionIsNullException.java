package org.y_lab.application.exceptions;

public class ConnectionIsNullException extends RuntimeException{
    public ConnectionIsNullException() {
        super("Connection is null");
    }
}
