package com.homescout.filter;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;
import com.homescout.model.Residential;

public class BedroomFilter implements PropertyFilter {
    private final int minBedrooms;

    public BedroomFilter(int minBedrooms) {
        if (minBedrooms < 0) throw new InvalidInputException("Bedrooms cannot be negative");
        this.minBedrooms = minBedrooms;
    }

    @Override
    public boolean matches(Property p) {
        // INSTANCEOF + pattern matching: only residential properties have bedrooms
        return p instanceof Residential r && r.getBedrooms() >= minBedrooms;
    }
}
