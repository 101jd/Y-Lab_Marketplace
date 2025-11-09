package org.y_lab.application.exceptions;

public class QtyLessThanZeroRuntimeException extends RuntimeException {
    public QtyLessThanZeroRuntimeException() {
        super("Qty can't be less than zero");
    }
}
