package com.homescout.service;

import com.homescout.model.Property;

/** SINGLETON PATTERN: the whole application shares exactly one PropertyCatalog. */
public final class PropertyCatalog extends Catalog<Property> {
    private static PropertyCatalog instance;

    private PropertyCatalog() { }                       // nobody else can call new PropertyCatalog()

    public static synchronized PropertyCatalog getInstance() {
        if (instance == null) instance = new PropertyCatalog();
        return instance;
    }
}
