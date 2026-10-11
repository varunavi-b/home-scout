package com.homescout.exception;

/** Unchecked exception: a price broke a business rule (e.g. negative or too small). */
public class InvalidPriceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidPriceException(String message) {
        super(message);
    }
}
