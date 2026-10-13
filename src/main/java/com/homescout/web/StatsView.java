package com.homescout.web;

import com.homescout.model.Admin;
import com.homescout.model.Agent;
import com.homescout.model.Buyer;
import com.homescout.model.ListingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.service.Catalog;
import com.homescout.service.EnquiryService;
import com.homescout.service.SearchService;
import com.homescout.service.UserService;
import com.homescout.util.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** All the numbers for the admin statistics page. */
public class StatsView {

    /** STATIC NESTED CLASS: one bar of a bar chart. */
    public static class Bar {
        private final String label;
        private final String valueText;
        private final int percent;

        Bar(String label, String valueText, int percent) {
            this.label = label;
            this.valueText = valueText;
            this.percent = percent;
        }

        public String getLabel() { return label; }
        public String getValueText() { return valueText; }
        public int getPercent() { return percent; }
    }

    private final int totalListings;
    private final int available;
    private final int sold;
    private final int rented;
    private final String availableValue;
    private final String averagePrice;
    private final String mostExpensive;
    private final String cheapest;
    private final List<Bar> priceByCity;
    private final List<Bar> countByType;
    private final int buyers;
    private final int agents;
    private final int admins;
    private final int enquiryCount;
    private final int favouriteCount;

    private StatsView(SearchService search, Catalog<Property> catalog, UserService users, EnquiryService enquiries) {
        List<Property> all = catalog.getAll();
        this.totalListings = all.size();
        this.available = count(all, ListingStatus.AVAILABLE);
        this.sold = count(all, ListingStatus.SOLD);
        this.rented = count(all, ListingStatus.RENTED);
        this.availableValue = Money.format(all.stream()
                .filter(p -> p.getStatus().isOpen())
                .mapToDouble(Property::getPrice).sum());
        this.averagePrice = all.isEmpty() ? "-"
                : Money.format(all.stream().mapToDouble(Property::getPrice).average().orElse(0));
        // Collections.max / min use Property.compareTo(): your Comparable implementation
        this.mostExpensive = all.isEmpty() ? "-" : describe(Collections.max(all));
        this.cheapest = all.isEmpty() ? "-" : describe(Collections.min(all));

        this.priceByCity = bars(search.averagePriceByCity(), Money::format);

        Map<String, Double> typeCounts = new LinkedHashMap<>();
        for (Map.Entry<PropertyType, Long> e : search.countByType().entrySet()) {
            typeCounts.put(e.getKey().getLabel(), e.getValue().doubleValue());
        }
        this.countByType = bars(typeCounts, v -> String.valueOf(v.intValue()));

        this.buyers = users.getAllOfType(Buyer.class).size();
        this.agents = users.getAllOfType(Agent.class).size();
        this.admins = users.getAllOfType(Admin.class).size();
        this.enquiryCount = enquiries.all().size();
        this.favouriteCount = users.getAllOfType(Buyer.class).stream()
                .mapToInt(b -> b.getFavourites().size()).sum();
    }

    public static StatsView build(SearchService search, Catalog<Property> catalog,
                                  UserService users, EnquiryService enquiries) {
        return new StatsView(search, catalog, users, enquiries);
    }

    private static int count(List<Property> all, ListingStatus status) {
        return (int) all.stream().filter(p -> p.getStatus() == status).count();
    }

    private static String describe(Property p) {
        return p.getTitle() + " (" + Money.format(p.getPrice()) + ", " + p.getCity() + ")";
    }

    private static List<Bar> bars(Map<String, Double> values, Function<Double, String> format) {
        double max = values.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        List<Bar> bars = new ArrayList<>();
        for (Map.Entry<String, Double> e : values.entrySet()) {
            int percent = (max <= 0) ? 0 : (int) Math.round(e.getValue() / max * 100);
            bars.add(new Bar(e.getKey(), format.apply(e.getValue()), percent));
        }
        return bars;
    }

    public int getTotalListings() { return totalListings; }
    public int getAvailable() { return available; }
    public int getSold() { return sold; }
    public int getRented() { return rented; }
    public String getAvailableValue() { return availableValue; }
    public String getAveragePrice() { return averagePrice; }
    public String getMostExpensive() { return mostExpensive; }
    public String getCheapest() { return cheapest; }
    public List<Bar> getPriceByCity() { return priceByCity; }
    public List<Bar> getCountByType() { return countByType; }
    public int getBuyers() { return buyers; }
    public int getAgents() { return agents; }
    public int getAdmins() { return admins; }
    public int getEnquiryCount() { return enquiryCount; }
    public int getFavouriteCount() { return favouriteCount; }
}