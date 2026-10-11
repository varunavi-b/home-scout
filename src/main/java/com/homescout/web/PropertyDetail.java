package com.homescout.web;

import com.homescout.model.Apartment;
import com.homescout.model.Commercial;
import com.homescout.model.House;
import com.homescout.model.Land;
import com.homescout.model.Office;
import com.homescout.model.Property;
import com.homescout.model.Residential;
import com.homescout.model.Shop;
import com.homescout.model.Villa;
import com.homescout.util.Money;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Everything the detail page needs, already turned into plain text. */
public class PropertyDetail {
    private final int id;
    private final String title;
    private final String type;
    private final String className;
    private final String address;
    private final String price;
    private final String area;
    private final String pricePerSqFt;
    private final String tax;
    private final String taxRate;
    private final String status;
    private final String statusClass;
    private final String furnishing;
    private final String listedOn;
    private final List<String> amenities;
    private final List<String> specifics = new ArrayList<>();
    private final String summary;

    private PropertyDetail(Property p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.type = p.getType().getLabel();
        this.className = p.getClass().getSimpleName();
        this.address = p.getAddress().toString();
        this.price = Money.format(p.getPrice());
        this.area = String.format(Locale.US, "%.0f sq ft", p.getArea());
        this.pricePerSqFt = Money.format(p.getPricePerSqFt());
        this.tax = Money.format(p.calculateTax());                     // RUNTIME POLYMORPHISM
        this.taxRate = String.format(Locale.US, "%.0f%%", p.calculateTax() / p.getPrice() * 100);
        this.status = p.getStatus().getLabel();
        this.statusClass = switch (p.getStatus()) {
            case AVAILABLE -> "success";
            case SOLD -> "danger";
            case RENTED -> "warning";
        };
        this.furnishing = p.getFurnishing().getLabel();
        this.listedOn = p.getListedOn().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
        this.amenities = new ArrayList<>(p.getAmenities());
        this.summary = p.getDetails();                                  // overridden down the hierarchy

        // INSTANCEOF + casting: ask each kind of property for its own special details
        if (p instanceof Residential r) {
            specifics.add("Bedrooms: " + r.getBedrooms());
            specifics.add("Bathrooms: " + r.getBathrooms());
        }
        if (p instanceof Apartment a) {
            specifics.add("Floor: " + a.getFloor());
            specifics.add("Lift: " + yesNo(a.hasLift()));
        } else if (p instanceof House h) {
            specifics.add("Garden: " + yesNo(h.hasGarden()));
        } else if (p instanceof Villa v) {
            specifics.add("Private pool: " + yesNo(v.hasPool()));
        } else if (p instanceof Commercial c) {
            specifics.add("Parking spaces: " + c.getParkingSpaces());
            if (c instanceof Office o) {
                specifics.add("Conference room: " + yesNo(o.hasConferenceRoom()));
            } else if (c instanceof Shop s) {
                specifics.add("Road facing: " + yesNo(s.isRoadFacing()));
            }
        } else if (p instanceof Land l) {
            specifics.add("Plot type: " + l.getPlotType());
            specifics.add("Fenced: " + yesNo(l.isFenced()));
        }
    }

    public static PropertyDetail from(Property p) {
        return new PropertyDetail(p);
    }

    private static String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getClassName() { return className; }
    public String getAddress() { return address; }
    public String getPrice() { return price; }
    public String getArea() { return area; }
    public String getPricePerSqFt() { return pricePerSqFt; }
    public String getTax() { return tax; }
    public String getTaxRate() { return taxRate; }
    public String getStatus() { return status; }
    public String getStatusClass() { return statusClass; }
    public String getFurnishing() { return furnishing; }
    public String getListedOn() { return listedOn; }
    public List<String> getAmenities() { return amenities; }
    public List<String> getSpecifics() { return specifics; }
    public String getSummary() { return summary; }
}
