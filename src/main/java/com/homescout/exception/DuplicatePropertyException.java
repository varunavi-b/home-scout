package com.homescout.exception;

/** Unchecked exception: a property with the same id is already in the catalog. */
public class DuplicatePropertyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicatePropertyException(int id) {
        super("A property with id " + id + " already exists in the catalog");
    }
}
