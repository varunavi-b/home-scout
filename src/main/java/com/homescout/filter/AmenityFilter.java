package com.homescout.filter;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;

import java.util.ArrayList;
import java.util.List;

/** Property must have ALL of the required amenities. */
public class AmenityFilter implements PropertyFilter {
    private final List<String> required = new ArrayList<>();

    public AmenityFilter(String... amenities) {         // VARARGS
        if (amenities == null || amenities.length == 0) throw new InvalidInputException("Choose at least one amenity");
        for (String a : amenities) {
            if (a != null && !a.isBlank()) required.add(a.trim());
        }
        if (required.isEmpty()) throw new InvalidInputException("Choose at least one amenity");
    }

    @Override
    public boolean matches(Property p) {
        return required.stream().allMatch(p::hasAmenity);
    }
}
