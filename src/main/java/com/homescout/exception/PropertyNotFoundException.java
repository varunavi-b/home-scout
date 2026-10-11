package com.homescout.exception;

/** Checked exception: the caller MUST handle or declare it (shows checked vs unchecked). */
public class PropertyNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public PropertyNotFoundException(int id) {
        super("No property found with id " + id);
    }
}
