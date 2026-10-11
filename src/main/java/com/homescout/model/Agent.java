package com.homescout.model;

import com.homescout.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Agent extends User {
    private static final long serialVersionUID = 1L;

    private final double commissionRate;                              // e.g. 0.02 = 2%
    private final List<Property> listings = new ArrayList<>();        // AGGREGATION

    public Agent(String name, String email, double commissionRate) {
        super(name, email);
        if (commissionRate < 0 || commissionRate > 0.1) {
            throw new InvalidInputException("Commission rate must be between 0 and 0.10");
        }
        this.commissionRate = commissionRate;
    }

    public void addListing(Property p) {
        p.setAgentId(getId());           // the agent becomes the owner
        listings.add(p);
    }

    public boolean removeListing(int propertyId) {
        return listings.removeIf(p -> p.getId() == propertyId);
    }

    public List<Property> getListings() { return Collections.unmodifiableList(listings); }

    public double getCommissionRate() { return commissionRate; }

    public double calculateCommission(Property p) { return p.getPrice() * commissionRate; }

    @Override
    public String getRole() { return "Agent"; }

    @Override
    public boolean canManage(Property property) { return property.getAgentId() == getId(); }   // own listings only
}
