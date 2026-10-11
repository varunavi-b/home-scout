package com.homescout.filter;

import com.homescout.model.ListingStatus;
import com.homescout.model.Property;

public class StatusFilter implements PropertyFilter {
    private final ListingStatus wanted;

    public StatusFilter(ListingStatus wanted) {
        this.wanted = wanted;
    }

    @Override
    public boolean matches(Property p) {
        return p.getStatus() == wanted;
    }
}
