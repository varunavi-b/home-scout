package com.homescout.filter;

import com.homescout.model.FurnishingStatus;
import com.homescout.model.ListingStatus;
import com.homescout.model.PropertyType;
import com.homescout.util.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * BUILDER PATTERN with a STATIC NESTED CLASS.
 *
 *   CompositeFilter f = FilterCriteria.builder()
 *           .city("Jaipur").type(PropertyType.VILLA).minBedrooms(3).build();
 */
public final class FilterCriteria {

    private FilterCriteria() { }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<PropertyFilter> filters = new ArrayList<>();
        private final List<String> descriptions = new ArrayList<>();
        private CompositeFilter.Mode mode = CompositeFilter.Mode.AND;

        private Builder add(PropertyFilter f, String description) {
            filters.add(f);
            descriptions.add(description);
            return this;
        }

        public Builder city(String city) {
            return (city == null || city.isBlank()) ? this : add(new CityFilter(city), "city = " + city.trim());
        }

        public Builder type(PropertyType... types) {
            if (types == null || types.length == 0) return this;
            StringBuilder names = new StringBuilder();
            for (PropertyType t : types) names.append(names.length() == 0 ? "" : "/").append(t.getLabel());
            return add(new TypeFilter(types), "type = " + names);
        }

        public Builder price(double min, double max) {
            String maxText = (max == Double.MAX_VALUE) ? "any" : Money.format(max);
            return add(new PriceRangeFilter(min, max), "price " + Money.format(min) + " to " + maxText);
        }

        public Builder minBedrooms(int beds) {
            return beds <= 0 ? this : add(new BedroomFilter(beds), "bedrooms >= " + beds);
        }

        public Builder area(double min, double max) {
            return add(new AreaFilter(min, max), "area " + (long) min + " to " + (long) max + " sq ft");
        }

        public Builder amenities(String... names) {
            if (names == null || names.length == 0) return this;
            return add(new AmenityFilter(names), "amenities = " + String.join(", ", names).trim());
        }

        public Builder furnishing(FurnishingStatus status) {
            return status == null ? this : add(new FurnishingFilter(status), "furnishing = " + status.getLabel());
        }

        public Builder status(ListingStatus status) {
            return status == null ? this : add(new StatusFilter(status), "status = " + status.getLabel());
        }

        public Builder onlyAvailable() {
            return status(ListingStatus.AVAILABLE);
        }

        public Builder keyword(String keyword) {
            return (keyword == null || keyword.isBlank()) ? this : add(new KeywordFilter(keyword), "keyword = " + keyword.trim());
        }

        /** Switch from "all criteria must match" (AND) to "any criterion may match" (OR). */
        public Builder matchAny() {
            this.mode = CompositeFilter.Mode.OR;
            return this;
        }

        public CompositeFilter build() {
            CompositeFilter composite = new CompositeFilter(mode);
            filters.forEach(composite::add);
            return composite;
        }

        public String describe() {
            if (descriptions.isEmpty()) return "(no filters - showing everything)";
            return String.join(mode == CompositeFilter.Mode.AND ? " AND " : " OR ", descriptions);
        }
    }
}
