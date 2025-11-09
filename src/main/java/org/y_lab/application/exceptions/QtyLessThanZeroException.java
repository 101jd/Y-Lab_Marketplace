package org.y_lab.application.exceptions;

public class QtyLessThanZeroException extends Exception{
    public QtyLessThanZeroException() {
        super("Qty can't be less than zero");
    }
}
