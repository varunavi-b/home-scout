package com.homescout.model;

import com.homescout.exception.InvalidInputException;

public class Apartment extends Residential {
    private static final long serialVersionUID = 1L;
    private static final double TAX_RATE = 0.05;     // FINAL constant

    private final int floor;
    private final boolean hasLift;

    public Apartment(String title, Address address, double price, double area,
                     int bedrooms, int bathrooms, int floor, boolean hasLift) {
        super(title, address, price, area, bedrooms, bathrooms);
        if (floor < 0) throw new InvalidInputException("Floor cannot be negative");
        this.floor = floor;
        this.hasLift = hasLift;
    }

    /** OVERLOADED constructor: THIS(...) chains to the full constructor above. */
    public Apartment(String title, Address address, double price, double area, int bedrooms) {
        this(title, address, price, area, bedrooms, 1, 0, false);
    }

    public int getFloor() { return floor; }
    public boolean hasLift() { return hasLift; }

    @Override
    public double calculateTax() { return getPrice() * TAX_RATE; }       // RUNTIME POLYMORPHISM

    @Override
    public PropertyType getType() { return PropertyType.APARTMENT; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | floor " + floor + ", lift: " + (hasLift ? "yes" : "no");
    }
}
