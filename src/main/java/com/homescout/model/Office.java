package com.homescout.model;

public class Office extends Commercial {
    private static final long serialVersionUID = 1L;
    private static final double TAX_RATE = 0.10;

    private final boolean hasConferenceRoom;

    public Office(String title, Address address, double price, double area,
                  int parkingSpaces, boolean hasConferenceRoom) {
        super(title, address, price, area, parkingSpaces);
        this.hasConferenceRoom = hasConferenceRoom;
    }

    public boolean hasConferenceRoom() { return hasConferenceRoom; }

    @Override
    public double calculateTax() { return getPrice() * TAX_RATE; }

    @Override
    public PropertyType getType() { return PropertyType.OFFICE; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | conference room: " + (hasConferenceRoom ? "yes" : "no");
    }
}
