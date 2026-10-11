package com.homescout.model;

import com.homescout.exception.InvalidInputException;

/** ABSTRACT intermediate class: Property -> Commercial -> (Office | Shop). */
public abstract class Commercial extends Property {
    private static final long serialVersionUID = 1L;

    private int parkingSpaces;

    protected Commercial(String title, Address address, double price, double area, int parkingSpaces) {
        super(title, address, price, area);
        setParkingSpaces(parkingSpaces);
    }

    public final void setParkingSpaces(int parkingSpaces) {
        if (parkingSpaces < 0) throw new InvalidInputException("Parking spaces cannot be negative");
        this.parkingSpaces = parkingSpaces;
    }

    public int getParkingSpaces() { return parkingSpaces; }

    @Override
    public String getDetails() {
        return super.getDetails() + " | parking: " + parkingSpaces;
    }
}
