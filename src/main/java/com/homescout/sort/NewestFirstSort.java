package com.homescout.sort;

import com.homescout.model.Property;

public class NewestFirstSort implements SortStrategy {
    @Override
    public int compare(Property a, Property b) {
        int byDate = b.getListedOn().compareTo(a.getListedOn());
        return byDate != 0 ? byDate : Integer.compare(b.getId(), a.getId());
    }

    @Override
    public String label() { return "Newest first"; }
}
