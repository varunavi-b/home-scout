package com.homescout.web;

import com.homescout.model.Commercial;
import com.homescout.model.Land;
import com.homescout.model.Property;
import com.homescout.model.Residential;
import com.homescout.util.Money;

import java.util.Locale;

public class PropertyCard {
    private final int id;
    private final String title;
    private final String type;
    private final String city;
    private final String price;
    private final String area;
    private final String features;
    private final String status;
    private final String statusClass;

    private PropertyCard(Property p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.type = p.getType().getLabel();
        this.city = p.getCity();
        this.price = Money.format(p.getPrice());
        this.area = String.format(Locale.US, "%.0f sq ft", p.getArea());
        this.status = p.getStatus().getLabel();
        this.statusClass = switch (p.getStatus()) {
            case AVAILABLE -> "success";
            case SOLD -> "danger";
            case RENTED -> "warning";
        };
        if (p instanceof Residential r) {
            this.features = r.getBedrooms() + " BHK, " + r.getBathrooms() + " bath";
        } else if (p instanceof Commercial c) {
            this.features = c.getParkingSpaces() + " parking";
        } else if (p instanceof Land l) {
            this.features = l.getPlotType();
        } else {
            this.features = "";
        }
    }

    public static PropertyCard from(Property p) {
        return new PropertyCard(p);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getCity() { return city; }
    public String getPrice() { return price; }
    public String getArea() { return area; }
    public String getFeatures() { return features; }
    public String getStatus() { return status; }
    public String getStatusClass() { return statusClass; }
}