package com.homescout.sort;

import com.homescout.model.Property;

public class PriceAscendingSort implements SortStrategy {
    @Override
    public int compare(Property a, Property b) { return a.compareTo(b); }   // natural order

    @Override
    public String label() { return "Price: low to high"; }
}
