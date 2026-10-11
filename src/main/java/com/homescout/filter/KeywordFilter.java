package com.homescout.filter;

import com.homescout.model.Property;
import com.homescout.model.Searchable;

public class KeywordFilter implements PropertyFilter {
    private final String keyword;

    public KeywordFilter(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean matches(Property p) {
        Searchable s = p;                 // upcast to the Searchable interface
        return s.matchesKeyword(keyword);
    }
}
