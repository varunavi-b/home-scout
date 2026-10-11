package com.homescout.model;

/**
 * INTERFACE with a DEFAULT method. Property implements this together with
 * Comparable, Searchable and Serializable (multiple inheritance of type via interfaces).
 */
public interface Bookmarkable {
    int getId();

    String getTitle();

    default String bookmarkLabel() {
        return "[Saved] #" + getId() + " " + getTitle();
    }
}
