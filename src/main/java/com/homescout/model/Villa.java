package com.homescout.model;

/** MULTILEVEL inheritance: Property -> Residential -> Villa. */
public class Villa extends Residential {
    private static final long serialVersionUID = 1L;
    private static final double TAX_RATE = 0.08;     // luxury: higher tax rate

    private final boolean hasPool;

    public Villa(String title, Address address, double price, double area,
                 int bedrooms, int bathrooms, boolean hasPool) {
        super(title, address, price, area, bedrooms, bathrooms);
        this.hasPool = hasPool;
    }

    public boolean hasPool() { return hasPool; }

    @Override
    public double calculateTax() { return getPrice() * TAX_RATE; }

    @Override
    public PropertyType getType() { return PropertyType.VILLA; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | pool: " + (hasPool ? "yes" : "no");
    }
}
