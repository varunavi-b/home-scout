package com.homescout.web;

import com.homescout.model.Address;
import com.homescout.model.Commercial;
import com.homescout.model.FurnishingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.Residential;
import com.homescout.util.PropertyFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/** Holds what an agent typed into the add / edit listing form, and checks it. */
public class PropertyForm {
    private static final double LAKH = 100_000;

    private String type = "Apartment";
    private String title;
    private String street;
    private String city;
    private String state = "Rajasthan";
    private String pincode;
    private Double priceLakh;
    private Double area;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floor;
    private Integer parking;
    private boolean lift;
    private boolean garden;
    private boolean pool;
    private boolean conference;
    private boolean roadFacing;
    private boolean fenced;
    private String plotType;
    private String furnishing;
    private String amenities;          // comma separated

    // ================= VALIDATION (returns field name -> message) =================

    public Map<String, String> validateForCreate() {
        Map<String, String> e = new LinkedHashMap<>();
        PropertyType t = null;
        try {
            t = PropertyType.fromText(type);
        } catch (RuntimeException ex) {
            e.put("type", "Choose a property type");
        }
        requireText(e, "title", title, "Title is required");
        requireText(e, "street", street, "Street is required");
        requireText(e, "city", city, "City is required");
        requireText(e, "state", state, "State is required");
        if (pincode == null || !pincode.trim().matches("\\d{6}")) {
            e.put("pincode", "Pincode must be exactly 6 digits");
        }
        checkPriceAndArea(e);

        if (t != null) {
            if (t.isResidential()) {
                checkRange(e, "bedrooms", bedrooms, 1, 20, "Bedrooms must be 1 to 20");
                checkRange(e, "bathrooms", bathrooms, 1, 20, "Bathrooms must be 1 to 20");
            }
            if (t == PropertyType.APARTMENT) {
                checkRange(e, "floor", floor, 0, 100, "Floor must be 0 to 100");
            }
            if (t == PropertyType.OFFICE || t == PropertyType.SHOP) {
                checkRange(e, "parking", parking, 0, 100, "Parking must be 0 to 100");
            }
            if (t == PropertyType.LAND) {
                requireText(e, "plotType", plotType, "Plot type is required");
            }
        }
        return e;
    }

    public Map<String, String> validateForEdit(Property p) {
        Map<String, String> e = new LinkedHashMap<>();
        requireText(e, "title", title, "Title is required");
        checkPriceAndArea(e);
        if (p instanceof Residential) {
            checkRange(e, "bedrooms", bedrooms, 1, 20, "Bedrooms must be 1 to 20");
            checkRange(e, "bathrooms", bathrooms, 1, 20, "Bathrooms must be 1 to 20");
        }
        if (p instanceof Commercial) {
            checkRange(e, "parking", parking, 0, 100, "Parking must be 0 to 100");
        }
        return e;
    }

    private void checkPriceAndArea(Map<String, String> e) {
        if (priceLakh == null || priceLakh <= 0) {
            e.put("priceLakh", "Enter a price above 0");
        } else if (priceLakh * LAKH < Property.MIN_PRICE) {
            e.put("priceLakh", "That price is too low");
        } else if (priceLakh > 1_000_000) {
            e.put("priceLakh", "That price is too high");
        }
        if (area == null || area <= 0) {
            e.put("area", "Enter an area above 0");
        } else if (area > 1_000_000) {
            e.put("area", "That area is too large");
        }
    }

    private static void requireText(Map<String, String> e, String field, String value, String message) {
        if (value == null || value.isBlank()) e.put(field, message);
    }

    private static void checkRange(Map<String, String> e, String field, Integer value, int min, int max, String message) {
        if (value == null || value < min || value > max) e.put(field, message);
    }

    // ================= BUILDING / UPDATING a Property =================

    /** Call only after validateForCreate() found no errors. Uses your Factory pattern. */
    public Property toProperty() {
        PropertyType t = PropertyType.fromText(type);
        Address address = new Address(street, city, state, pincode);
        double price = priceLakh * LAKH;
        int beds = (bedrooms == null) ? 0 : bedrooms;
        int baths = (bathrooms == null) ? 0 : bathrooms;

        Property p;
        switch (t) {
            case APARTMENT -> p = PropertyFactory.create(t, title, address, price, area, beds, baths, floor, lift);
            case HOUSE -> p = PropertyFactory.create(t, title, address, price, area, beds, baths, 0, garden);
            case VILLA -> p = PropertyFactory.create(t, title, address, price, area, beds, baths, 0, pool);
            case OFFICE -> p = PropertyFactory.create(t, title, address, price, area, 0, 0, parking, conference);
            case SHOP -> p = PropertyFactory.create(t, title, address, price, area, 0, 0, parking, roadFacing);
            default -> p = PropertyFactory.createLand(title, address, price, area, plotType, fenced);
        }
        p.setFurnishing(FurnishingStatus.fromText(furnishing));
        addAmenitiesTo(p);
        return p;
    }

    /** Applies the editable fields to an existing property (its setters validate again). */
    public void applyTo(Property p) {
        p.setTitle(title);
        p.setPrice(priceLakh * LAKH);
        p.setArea(area);
        p.setFurnishing(FurnishingStatus.fromText(furnishing));
        if (p instanceof Residential r) {
            r.setBedrooms(bedrooms);
            r.setBathrooms(bathrooms);
        }
        if (p instanceof Commercial c) {
            c.setParkingSpaces(parking);
        }
        addAmenitiesTo(p);
    }

    /** Fills the edit form from an existing property. */
    public static PropertyForm from(Property p) {
        PropertyForm f = new PropertyForm();
        f.title = p.getTitle();
        f.priceLakh = p.getPrice() / LAKH;
        f.area = p.getArea();
        f.furnishing = switch (p.getFurnishing()) {
            case FURNISHED -> "Furnished";
            case SEMI_FURNISHED -> "Semi";
            case UNFURNISHED -> "Unfurnished";
        };
        if (p instanceof Residential r) {
            f.bedrooms = r.getBedrooms();
            f.bathrooms = r.getBathrooms();
        }
        if (p instanceof Commercial c) {
            f.parking = c.getParkingSpaces();
        }
        return f;
    }

    private void addAmenitiesTo(Property p) {
        if (amenities != null && !amenities.isBlank()) {
            p.addAmenities(amenities.split(","));          // varargs called with an array
        }
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public Double getPriceLakh() { return priceLakh; }
    public void setPriceLakh(Double priceLakh) { this.priceLakh = priceLakh; }
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public Integer getParking() { return parking; }
    public void setParking(Integer parking) { this.parking = parking; }
    public boolean isLift() { return lift; }
    public void setLift(boolean lift) { this.lift = lift; }
    public boolean isGarden() { return garden; }
    public void setGarden(boolean garden) { this.garden = garden; }
    public boolean isPool() { return pool; }
    public void setPool(boolean pool) { this.pool = pool; }
    public boolean isConference() { return conference; }
    public void setConference(boolean conference) { this.conference = conference; }
    public boolean isRoadFacing() { return roadFacing; }
    public void setRoadFacing(boolean roadFacing) { this.roadFacing = roadFacing; }
    public boolean isFenced() { return fenced; }
    public void setFenced(boolean fenced) { this.fenced = fenced; }
    public String getPlotType() { return plotType; }
    public void setPlotType(String plotType) { this.plotType = plotType; }
    public String getFurnishing() { return furnishing; }
    public void setFurnishing(String furnishing) { this.furnishing = furnishing; }
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
}
