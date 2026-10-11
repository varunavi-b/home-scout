package com.homescout.model;

public class House extends Residential {
    private static final long serialVersionUID = 1L;
    private static final double TAX_RATE = 0.06;

    private final boolean hasGarden;

    public House(String title, Address address, double price, double area,
                 int bedrooms, int bathrooms, boolean hasGarden) {
        super(title, address, price, area, bedrooms, bathrooms);
        this.hasGarden = hasGarden;
    }

    public boolean hasGarden() { return hasGarden; }

    @Override
    public double calculateTax() { return getPrice() * TAX_RATE; }

    @Override
    public PropertyType getType() { return PropertyType.HOUSE; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | garden: " + (hasGarden ? "yes" : "no");
    }
}
