package com.homescout.filter;

import com.homescout.model.Property;

/**
 * INTERFACE (abstraction) - the heart of the search engine.
 * It has ONE abstract method, so it is also a functional interface (lambda friendly).
 */
@FunctionalInterface
public interface PropertyFilter {

    boolean matches(Property property);

    // ---- DEFAULT methods: behaviour provided by the interface itself ----
    default PropertyFilter and(PropertyFilter other) {
        return p -> this.matches(p) && other.matches(p);
    }

    default PropertyFilter or(PropertyFilter other) {
        return p -> this.matches(p) || other.matches(p);
    }

    default PropertyFilter negate() {
        return p -> !this.matches(p);
    }

    // ---- STATIC interface method ----
    static PropertyFilter acceptAll() {
        return p -> true;
    }
}
