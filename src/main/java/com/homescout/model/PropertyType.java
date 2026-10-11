package com.homescout.model;

import com.homescout.exception.InvalidInputException;

/** ENUM with fields, a constructor and methods (not just constants). */
public enum PropertyType {
    APARTMENT("Apartment", true),
    HOUSE("House", true),
    VILLA("Villa", true),
    OFFICE("Office", false),
    SHOP("Shop", false),
    LAND("Land", false);

    private final String label;
    private final boolean residential;

    PropertyType(String label, boolean residential) {
        this.label = label;
        this.residential = residential;
    }

    public String getLabel() { return label; }

    public boolean isResidential() { return residential; }

    /** Converts user text such as "villa" or "Villa" into the enum constant. */
    public static PropertyType fromText(String text) {
        if (text != null) {
            for (PropertyType t : values()) {
                if (t.label.equalsIgnoreCase(text.trim()) || t.name().equalsIgnoreCase(text.trim())) {
                    return t;
                }
            }
        }
        throw new InvalidInputException("Unknown property type: " + text);
    }
}
