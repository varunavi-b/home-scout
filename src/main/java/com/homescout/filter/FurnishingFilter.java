package com.homescout.filter;

import com.homescout.model.FurnishingStatus;
import com.homescout.model.Property;

public class FurnishingFilter implements PropertyFilter {
    private final FurnishingStatus wanted;

    public FurnishingFilter(FurnishingStatus wanted) {
        this.wanted = wanted;
    }

    @Override
    public boolean matches(Property p) {
        return p.getFurnishing() == wanted;
    }
}
