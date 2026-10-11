package com.homescout.exception;

/** Checked exception wrapping low-level I/O problems in the persistence layer. */
public class DataFileException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
