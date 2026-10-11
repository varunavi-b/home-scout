package com.homescout.web;

import com.homescout.filter.FilterCriteria;
import com.homescout.model.FurnishingStatus;
import com.homescout.model.PropertyType;
import com.homescout.sort.AreaDescendingSort;
import com.homescout.sort.NewestFirstSort;
import com.homescout.sort.PriceAscendingSort;
import com.homescout.sort.PriceDescendingSort;
import com.homescout.sort.SortStrategy;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FilterForm {
    private static final double LAKH = 100_000;

    private String city;
    private List<String> types = new ArrayList<>();
    private Double minPrice;          // typed in lakh on the page
    private Double maxPrice;
    private Integer minBedrooms;
    private String furnishing;
    private List<String> amenities = new ArrayList<>();
    private String keyword;
    private boolean onlyAvailable;
    private boolean matchAny;         // false = ALL criteria (AND), true = ANY criterion (OR)
    private String sort = "priceAsc";
    private int page = 1;             // which page of results to show

    /** Turns the form into your Builder. Blank fields are simply skipped. */
    public FilterCriteria.Builder toCriteria() {
        FilterCriteria.Builder b = FilterCriteria.builder();
        b.city(city);
        if (types != null && !types.isEmpty()) {
            b.type(types.stream().map(PropertyType::fromText).toArray(PropertyType[]::new));
        }
        if (minPrice != null || maxPrice != null) {
            double low = (minPrice == null) ? 0 : minPrice * LAKH;
            double high = (maxPrice == null) ? Double.MAX_VALUE : maxPrice * LAKH;
            b.price(low, high);
        }
        if (minBedrooms != null) {
            b.minBedrooms(minBedrooms);
        }
        if (furnishing != null && !furnishing.isBlank()) {
            b.furnishing(FurnishingStatus.fromText(furnishing));
        }
        if (amenities != null && !amenities.isEmpty()) {
            b.amenities(amenities.toArray(new String[0]));
        }
        b.keyword(keyword);
        if (matchAny) {
            b.matchAny();
        }
        return b;
    }

    /** STRATEGY pattern: the chosen word decides which sorting object is used. */
    public SortStrategy sortStrategy() {
        return switch (sort == null ? "" : sort) {
            case "priceDesc" -> new PriceDescendingSort();
            case "areaDesc" -> new AreaDescendingSort();
            case "newest" -> new NewestFirstSort();
            default -> new PriceAscendingSort();
        };
    }

    /**
     * Rebuilds the current filters as a web-address query, e.g. "city=Jaipur&types=Villa&".
     * Every piece ends with "&" so the page number can simply be added after it.
     */
    public String toQueryString() {
        StringBuilder q = new StringBuilder();
        add(q, "city", city);
        if (types != null) {
            for (String t : types) add(q, "types", t);
        }
        if (minPrice != null) add(q, "minPrice", String.valueOf(minPrice));
        if (maxPrice != null) add(q, "maxPrice", String.valueOf(maxPrice));
        if (minBedrooms != null) add(q, "minBedrooms", String.valueOf(minBedrooms));
        add(q, "furnishing", furnishing);
        if (amenities != null) {
            for (String a : amenities) add(q, "amenities", a);
        }
        add(q, "keyword", keyword);
        if (onlyAvailable) add(q, "onlyAvailable", "true");
        if (matchAny) add(q, "matchAny", "true");
        add(q, "sort", sort);
        return q.toString();
    }

    private static void add(StringBuilder q, String name, String value) {
        if (value == null || value.isBlank()) return;
        q.append(name).append('=')
                .append(URLEncoder.encode(value.trim(), StandardCharsets.UTF_8))
                .append('&');
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }
    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }
    public String getFurnishing() { return furnishing; }
    public void setFurnishing(String furnishing) { this.furnishing = furnishing; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public boolean isOnlyAvailable() { return onlyAvailable; }
    public void setOnlyAvailable(boolean onlyAvailable) { this.onlyAvailable = onlyAvailable; }
    public boolean isMatchAny() { return matchAny; }
    public void setMatchAny(boolean matchAny) { this.matchAny = matchAny; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
}