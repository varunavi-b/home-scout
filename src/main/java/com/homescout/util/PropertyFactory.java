package com.homescout.util;

import com.homescout.model.Address;
import com.homescout.model.Apartment;
import com.homescout.model.House;
import com.homescout.model.Land;
import com.homescout.model.Office;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.Shop;
import com.homescout.model.Villa;

/**
 * FACTORY PATTERN: callers say WHICH type they want; the factory decides which class to build.
 *
 * Generic parameters:
 *   count -> floor (Apartment) or parking spaces (Office, Shop); ignored otherwise
 *   flag  -> hasLift / hasGarden / hasPool / hasConferenceRoom / roadFacing / fenced
 */
public final class PropertyFactory {

    private PropertyFactory() { }

    public static Property create(PropertyType type, String title, Address address, double price, double area,
                                  int bedrooms, int bathrooms, int count, boolean flag) {
        return switch (type) {
            case APARTMENT -> new Apartment(title, address, price, area, bedrooms, bathrooms, count, flag);
            case HOUSE     -> new House(title, address, price, area, bedrooms, bathrooms, flag);
            case VILLA     -> new Villa(title, address, price, area, bedrooms, bathrooms, flag);
            case OFFICE    -> new Office(title, address, price, area, count, flag);
            case SHOP      -> new Shop(title, address, price, area, count, flag);
            case LAND      -> createLand(title, address, price, area, "Residential plot", flag);
        };
    }

    public static Property createLand(String title, Address address, double price, double area,
                                      String plotType, boolean fenced) {
        return new Land(title, address, price, area, plotType, fenced);
    }
}
