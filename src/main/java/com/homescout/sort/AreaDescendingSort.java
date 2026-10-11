package com.homescout.sort;

import com.homescout.model.Property;

public class AreaDescendingSort implements SortStrategy {
    @Override
    public int compare(Property a, Property b) { return Double.compare(b.getArea(), a.getArea()); }

    @Override
    public String label() { return "Area: largest first"; }
}
