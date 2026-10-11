package com.homescout.filter;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;

public class CityFilter implements PropertyFilter {
    private final String city;

    public CityFilter(String city) {
        if (city == null || city.isBlank()) throw new InvalidInputException("City must not be empty");
        this.city = city.trim();
    }

    @Override
    public boolean matches(Property p) {
        return p.getCity().equalsIgnoreCase(city);
    }
}
