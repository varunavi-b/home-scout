package com.homescout.web;

import com.homescout.filter.FilterCriteria;
import com.homescout.model.FurnishingStatus;
import com.homescout.model.PropertyType;
import com.homescout.sort.AreaDescendingSort;
import com.homescout.sort.NewestFirstSort;
import com.homescout.sort.PriceAscendingSort;
import com.homescout.sort.PriceDescendingSort;
import com.homescout.sort.SortStrategy;

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
}
