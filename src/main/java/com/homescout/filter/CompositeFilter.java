package com.homescout.filter;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITE PATTERN: a filter that is made of other filters.
 * Because it is itself a PropertyFilter, composites can be nested inside composites.
 */
public class CompositeFilter implements PropertyFilter {

    /** Nested ENUM: how the child filters are combined. */
    public enum Mode { AND, OR }

    private final List<PropertyFilter> filters = new ArrayList<>();
    private final Mode mode;

    public CompositeFilter() {
        this(Mode.AND);
    }

    public CompositeFilter(Mode mode) {
        if (mode == null) throw new InvalidInputException("Mode is required");
        this.mode = mode;
    }

    /** Returns this so calls can be chained: new CompositeFilter().add(a).add(b). */
    public CompositeFilter add(PropertyFilter filter) {
        if (filter == null) throw new InvalidInputException("Filter must not be null");
        filters.add(filter);
        return this;
    }

    public int size() { return filters.size(); }

    public Mode getMode() { return mode; }

    @Override
    public boolean matches(Property p) {
        if (filters.isEmpty()) return true;       // no criteria means everything matches
        return (mode == Mode.AND)
                ? filters.stream().allMatch(f -> f.matches(p))
                : filters.stream().anyMatch(f -> f.matches(p));
    }
}
