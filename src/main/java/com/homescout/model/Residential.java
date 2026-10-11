package com.homescout.model;

import com.homescout.exception.InvalidInputException;

/** ABSTRACT intermediate class: Property -> Residential -> (Apartment | House | Villa). */
public abstract class Residential extends Property {
    private static final long serialVersionUID = 1L;

    private int bedrooms;
    private int bathrooms;

    protected Residential(String title, Address address, double price, double area,
                          int bedrooms, int bathrooms) {
        super(title, address, price, area);          // SUPER: calls Property constructor
        setBedrooms(bedrooms);
        setBathrooms(bathrooms);
    }

    public final void setBedrooms(int bedrooms) {
        if (bedrooms < 1 || bedrooms > 20) throw new InvalidInputException("Bedrooms must be between 1 and 20");
        this.bedrooms = bedrooms;
    }

    public final void setBathrooms(int bathrooms) {
        if (bathrooms < 1 || bathrooms > 20) throw new InvalidInputException("Bathrooms must be between 1 and 20");
        this.bathrooms = bathrooms;
    }

    public int getBedrooms() { return bedrooms; }
    public int getBathrooms() { return bathrooms; }

    @Override
    public String getDetails() {                     // OVERRIDING (level 1)
        return super.getDetails() + " | " + bedrooms + " BHK, " + bathrooms + " bath";
    }
}
