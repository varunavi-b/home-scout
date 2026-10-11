package com.homescout.service;

import com.homescout.exception.InvalidInputException;
import com.homescout.filter.CityFilter;
import com.homescout.filter.BedroomFilter;
import com.homescout.filter.CompositeFilter;
import com.homescout.filter.PriceRangeFilter;
import com.homescout.filter.PropertyFilter;
import com.homescout.filter.TypeFilter;
import com.homescout.model.ListingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.sort.SortStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SearchService {
    private final Catalog<Property> catalog;

    public SearchService(Catalog<Property> catalog) {
        this.catalog = catalog;
    }

    // ===== COMPILE-TIME POLYMORPHISM: same method name, different parameter lists =====

    public List<Property> search(String city) {
        return search(new CityFilter(city));
    }

    public List<Property> search(double minPrice, double maxPrice) {
        return search(new PriceRangeFilter(minPrice, maxPrice));
    }

    public List<Property> search(PropertyType type, int minBedrooms) {
        return search(new CompositeFilter().add(new TypeFilter(type)).add(new BedroomFilter(minBedrooms)));
    }

    /** The general version: any filter, including a CompositeFilter or a lambda. */
    public List<Property> search(PropertyFilter filter) {
        return catalog.getAll().stream()                 // LAMBDAS & STREAMS
                .filter(filter::matches)
                .collect(Collectors.toList());
    }

    public List<Property> search(PropertyFilter filter, SortStrategy sort) {
        List<Property> results = search(filter);
        results.sort(sort);
        return results;
    }

    // ===== pagination =====
    public List<Property> page(List<Property> list, int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) throw new InvalidInputException("Page number and size must be at least 1");
        int from = (pageNumber - 1) * pageSize;
        if (from >= list.size()) return new ArrayList<>();
        int to = Math.min(from + pageSize, list.size());
        return new ArrayList<>(list.subList(from, to));
    }

    // ===== ANONYMOUS INNER CLASS: a one-off Comparator =====
    public List<Property> sortByCityThenPrice(List<Property> list) {
        List<Property> copy = new ArrayList<>(list);
        copy.sort(new Comparator<Property>() {
            @Override
            public int compare(Property a, Property b) {
                int byCity = a.getCity().compareToIgnoreCase(b.getCity());
                return byCity != 0 ? byCity : Double.compare(a.getPrice(), b.getPrice());
            }
        });
        return copy;
    }

    // ===== statistics with streams =====
    public Map<String, Double> averagePriceByCity() {
        return catalog.getAll().stream().collect(Collectors.groupingBy(
                Property::getCity, TreeMap::new, Collectors.averagingDouble(Property::getPrice)));
    }

    public Map<PropertyType, Long> countByType() {
        return catalog.getAll().stream().collect(Collectors.groupingBy(
                Property::getType, () -> new EnumMap<PropertyType, Long>(PropertyType.class), Collectors.counting()));
    }

    /** Available properties within budget, closest to the budget first. */
    public List<Property> recommend(double budget, int limit) {
        return catalog.getAll().stream()
                .filter(p -> p.getStatus() == ListingStatus.AVAILABLE)
                .filter(p -> p.getPrice() <= budget)
                .sorted(Comparator.comparingDouble(Property::getPrice).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
