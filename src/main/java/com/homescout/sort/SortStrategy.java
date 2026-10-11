package com.homescout.sort;

import com.homescout.model.Property;

import java.util.Comparator;

/** STRATEGY PATTERN: every sorting rule is an interchangeable object. */
public interface SortStrategy extends Comparator<Property> {
    String label();
}
