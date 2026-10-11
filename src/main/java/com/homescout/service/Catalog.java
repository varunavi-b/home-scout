package com.homescout.service;

import com.homescout.exception.DuplicatePropertyException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.model.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GENERICS: a catalog that can hold any kind of Property (Catalog&lt;Property&gt;, Catalog&lt;Villa&gt; ...).
 * AGGREGATION: the catalog holds properties but does not own their life cycle.
 */
public class Catalog<T extends Property> {
    private final Map<Integer, T> items = new LinkedHashMap<>();

    public void add(T item) {
        if (items.containsKey(item.getId())) throw new DuplicatePropertyException(item.getId());
        items.put(item.getId(), item);
    }

    /** Checked exception: callers must handle "not found". */
    public T findById(int id) throws PropertyNotFoundException {
        T item = items.get(id);
        if (item == null) throw new PropertyNotFoundException(id);
        return item;
    }

    public T remove(int id) throws PropertyNotFoundException {
        T removed = items.remove(id);
        if (removed == null) throw new PropertyNotFoundException(id);
        return removed;
    }

    public boolean contains(int id) { return items.containsKey(id); }

    public int size() { return items.size(); }

    /** Returns a COPY so outside code cannot break the catalog's internal map. */
    public List<T> getAll() { return new ArrayList<>(items.values()); }

    public void replaceAll(Collection<? extends T> newItems) {
        items.clear();
        newItems.forEach(this::add);
    }
}
