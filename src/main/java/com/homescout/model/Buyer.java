package com.homescout.model;

import com.homescout.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Buyer extends User {
    private static final long serialVersionUID = 1L;

    private final double budget;
    private final List<Property> favourites = new ArrayList<>();   // AGGREGATION

    public Buyer(String name, String email, double budget) {
        super(name, email);
        if (budget <= 0) throw new InvalidInputException("Budget must be positive");
        this.budget = budget;
    }

    public double getBudget() { return budget; }

    public boolean addFavourite(Property p) {
        if (favourites.contains(p)) return false;
        return favourites.add(p);
    }

    public boolean removeFavourite(int propertyId) {
        return favourites.removeIf(p -> p.getId() == propertyId);
    }

    public List<Property> getFavourites() { return Collections.unmodifiableList(favourites); }

    @Override
    public String getRole() { return "Buyer"; }

    @Override
    public boolean canManage(Property property) { return false; }    // buyers cannot edit listings
}
