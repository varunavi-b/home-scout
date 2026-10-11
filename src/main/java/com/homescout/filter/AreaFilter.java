package com.homescout.filter;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;

public class AreaFilter implements PropertyFilter {
    private final double minArea;
    private final double maxArea;

    public AreaFilter(double minArea, double maxArea) {
        if (minArea < 0 || maxArea < minArea) throw new InvalidInputException("Invalid area range");
        this.minArea = minArea;
        this.maxArea = maxArea;
    }

    @Override
    public boolean matches(Property p) {
        return p.getArea() >= minArea && p.getArea() <= maxArea;
    }
}
