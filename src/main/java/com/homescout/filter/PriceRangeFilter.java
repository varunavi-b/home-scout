package com.homescout.filter;

import com.homescout.exception.InvalidPriceException;
import com.homescout.model.Property;

public class PriceRangeFilter implements PropertyFilter {
    private final double min;
    private final double max;

    public PriceRangeFilter(double min, double max) {
        if (min < 0 || max < min) throw new InvalidPriceException("Invalid price range: " + min + " to " + max);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(Property p) {
        return p.getPrice() >= min && p.getPrice() <= max;
    }
}
