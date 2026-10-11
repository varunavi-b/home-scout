package com.homescout.model;

import com.homescout.exception.InvalidInputException;

/** HIERARCHICAL inheritance: Residential, Commercial and Land all extend Property. */
public class Land extends Property {
    private static final long serialVersionUID = 1L;
    private static final double TAX_RATE = 0.04;

    private final String plotType;
    private final boolean fenced;

    public Land(String title, Address address, double price, double area, String plotType, boolean fenced) {
        super(title, address, price, area);
        if (plotType == null || plotType.isBlank()) throw new InvalidInputException("Plot type is required");
        this.plotType = plotType.trim();
        this.fenced = fenced;
    }

    public String getPlotType() { return plotType; }
    public boolean isFenced() { return fenced; }

    @Override
    public double calculateTax() { return getPrice() * TAX_RATE; }

    @Override
    public PropertyType getType() { return PropertyType.LAND; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | " + plotType + ", fenced: " + (fenced ? "yes" : "no");
    }
}
