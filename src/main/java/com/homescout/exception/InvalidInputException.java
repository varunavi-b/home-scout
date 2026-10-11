package com.homescout.exception;

/** Unchecked exception: any other invalid value (blank text, bad number, unknown type...). */
public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
