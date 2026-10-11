package com.homescout.model;

/** INTERFACE (abstraction): anything that can be matched against a keyword. */
public interface Searchable {
    boolean matchesKeyword(String keyword);
}
