package com.homescout.model;

import com.homescout.exception.InvalidInputException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** ASSOCIATION: links one Buyer to one Property. */
public class Enquiry {
    private static int counter = 0;

    private final int id;
    private final Buyer buyer;
    private final Property property;
    private final String message;
    private final LocalDateTime createdAt = LocalDateTime.now();

    public Enquiry(Buyer buyer, Property property, String message) {
        if (buyer == null || property == null) throw new InvalidInputException("Buyer and property are required");
        if (message == null || message.isBlank()) throw new InvalidInputException("Message must not be empty");
        this.id = ++counter;
        this.buyer = buyer;
        this.property = property;
        this.message = message.trim();
    }

    public int getId() { return id; }
    public Buyer getBuyer() { return buyer; }
    public Property getProperty() { return property; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "Enquiry #" + id + " from " + buyer.getName() + " on #" + property.getId() + " "
                + property.getTitle() + ": \"" + message + "\" ("
                + createdAt.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + ")";
    }
}
