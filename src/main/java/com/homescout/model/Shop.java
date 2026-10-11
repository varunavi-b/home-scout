package com.homescout.model;

public class Shop extends Commercial {
    private static final long serialVersionUID = 1L;
    private static final double TAX_RATE = 0.09;

    private final boolean roadFacing;

    public Shop(String title, Address address, double price, double area,
                int parkingSpaces, boolean roadFacing) {
        super(title, address, price, area, parkingSpaces);
        this.roadFacing = roadFacing;
    }

    public boolean isRoadFacing() { return roadFacing; }

    @Override
    public double calculateTax() { return getPrice() * TAX_RATE; }

    @Override
    public PropertyType getType() { return PropertyType.SHOP; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | road facing: " + (roadFacing ? "yes" : "no");
    }
}
