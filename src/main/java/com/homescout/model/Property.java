package com.homescout.model;

import com.homescout.exception.InvalidInputException;
import com.homescout.exception.InvalidPriceException;
import com.homescout.util.Money;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * ABSTRACTION: abstract class - cannot be instantiated, declares what every property must do.
 * ENCAPSULATION: private fields, access only through (validated) getters/setters.
 * MULTIPLE INTERFACES: Comparable, Searchable, Bookmarkable, Serializable.
 */
public abstract class Property implements Comparable<Property>, Searchable, Bookmarkable, Serializable {
    private static final long serialVersionUID = 1L;

    // STATIC: one counter shared by all properties
    private static int counter = 1000;

    // FINAL constant
    public static final double MIN_PRICE = 10_000;

    private final int id;                 // final: never changes after creation
    private String title;
    private final Address address;        // COMPOSITION: Property HAS-A Address
    private double price;
    private double area;                  // square feet
    private ListingStatus status = ListingStatus.AVAILABLE;
    private FurnishingStatus furnishing = FurnishingStatus.UNFURNISHED;
    private final List<String> amenities = new ArrayList<>();
    private final LocalDate listedOn = LocalDate.now();
    private int agentId;                  // 0 = no owner

    // ---------- constructors (OVERLOADED + constructor chaining with this) ----------
    protected Property(String title, Address address, double price, double area) {
        this(title, address, price, area, FurnishingStatus.UNFURNISHED);
    }

    protected Property(String title, Address address, double price, double area, FurnishingStatus furnishing) {
        this.id = ++counter;
        setTitle(title);
        if (address == null) throw new InvalidInputException("Address is required");
        this.address = address;
        setPrice(price);
        setArea(area);
        setFurnishing(furnishing);
    }

    // ---------- ABSTRACT methods: each subclass MUST define its own (runtime polymorphism) ----------
    public abstract double calculateTax();

    public abstract PropertyType getType();

    // ---------- validated setters (ENCAPSULATION) ----------
    public final void setTitle(String title) {
        if (title == null || title.isBlank()) throw new InvalidInputException("Title must not be empty");
        this.title = title.trim();
    }

    public final void setPrice(double price) {
        if (Double.isNaN(price) || price < MIN_PRICE) {
            throw new InvalidPriceException("Price must be at least Rs " + (long) MIN_PRICE + " but was " + price);
        }
        this.price = price;
    }

    public final void setArea(double area) {
        if (Double.isNaN(area) || area <= 0) throw new InvalidInputException("Area must be positive");
        this.area = area;
    }

    public final void setFurnishing(FurnishingStatus furnishing) {
        this.furnishing = (furnishing == null) ? FurnishingStatus.UNFURNISHED : furnishing;
    }

    public void setStatus(ListingStatus status) {
        if (status == null) throw new InvalidInputException("Status is required");
        this.status = status;
    }

    public void setAgentId(int agentId) { this.agentId = agentId; }

    // ---------- amenities: OVERLOADED methods incl. VARARGS ----------
    public void addAmenity(String amenity) {
        if (amenity != null && !amenity.isBlank() && !hasAmenity(amenity)) {
            amenities.add(amenity.trim());
        }
    }

    public void addAmenities(String... names) {
        for (String n : names) addAmenity(n);
    }

    public boolean hasAmenity(String amenity) {
        for (String a : amenities) {
            if (a.equalsIgnoreCase(amenity.trim())) return true;
        }
        return false;
    }

    // ---------- getters ----------
    public int getId() { return id; }
    @Override public String getTitle() { return title; }
    public Address getAddress() { return address; }
    public String getCity() { return address.getCity(); }
    public double getPrice() { return price; }
    public double getArea() { return area; }
    public ListingStatus getStatus() { return status; }
    public FurnishingStatus getFurnishing() { return furnishing; }
    public List<String> getAmenities() { return Collections.unmodifiableList(amenities); }
    public LocalDate getListedOn() { return listedOn; }
    public int getAgentId() { return agentId; }

    public double getPricePerSqFt() { return price / area; }

    // ---------- static helpers ----------
    /** Used when listings are loaded from disk so new ids never clash with old ones. */
    public static void syncCounter(int highestExistingId) {
        counter = Math.max(counter, highestExistingId);
    }

    public static int peekNextId() { return counter + 1; }

    // ---------- behaviour (overridden down the hierarchy) ----------
    public String getDetails() {
        return String.format(Locale.US, "#%d %s | %s | %s | %.0f sq ft | %s | %s",
                id, getType().getLabel(), title, address.getCity(), area, Money.format(price), status.getLabel());
    }

    @Override
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return true;
        String k = keyword.trim().toLowerCase();
        return title.toLowerCase().contains(k)
                || address.toString().toLowerCase().contains(k)
                || getType().getLabel().toLowerCase().contains(k);
    }

    // ---------- Object class methods ----------
    @Override
    public String toString() {
        return "#" + id + " [" + getType().getLabel() + "] " + title + " - " + address.getCity()
                + " - " + Money.format(price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        return id == ((Property) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    /** Natural ordering = by price (Comparable). */
    @Override
    public int compareTo(Property other) {
        int byPrice = Double.compare(this.price, other.price);
        return byPrice != 0 ? byPrice : Integer.compare(this.id, other.id);
    }
}
