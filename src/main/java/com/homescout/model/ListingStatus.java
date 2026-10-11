package com.homescout.model;

import com.homescout.exception.InvalidInputException;

public enum ListingStatus {
    AVAILABLE("Available"),
    SOLD("Sold"),
    RENTED("Rented");

    private final String label;

    ListingStatus(String label) { this.label = label; }

    public String getLabel() { return label; }

    /** Only AVAILABLE listings are still "open" for buyers. */
    public boolean isOpen() { return this == AVAILABLE; }

    public static ListingStatus fromText(String text) {
        for (ListingStatus s : values()) {
            if (text != null && (s.label.equalsIgnoreCase(text.trim()) || s.name().equalsIgnoreCase(text.trim()))) {
                return s;
            }
        }
        throw new InvalidInputException("Unknown status: " + text);
    }
}
