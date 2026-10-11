package com.homescout.sort;

import com.homescout.model.Property;

public class PriceDescendingSort implements SortStrategy {
    @Override
    public int compare(Property a, Property b) { return b.compareTo(a); }

    @Override
    public String label() { return "Price: high to low"; }
}
