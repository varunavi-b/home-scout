package com.homescout.filter;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class TypeFilter implements PropertyFilter {
    private final Set<PropertyType> types = EnumSet.noneOf(PropertyType.class);

    public TypeFilter(PropertyType... types) {          // VARARGS
        if (types == null || types.length == 0) throw new InvalidInputException("Choose at least one type");
        Collections.addAll(this.types, types);
    }

    @Override
    public boolean matches(Property p) {
        return types.contains(p.getType());
    }
}
