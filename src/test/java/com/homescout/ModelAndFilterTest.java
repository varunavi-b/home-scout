package com.homescout;

import com.homescout.exception.InvalidInputException;
import com.homescout.exception.InvalidPriceException;
import com.homescout.filter.AmenityFilter;
import com.homescout.filter.BedroomFilter;
import com.homescout.filter.CityFilter;
import com.homescout.filter.CompositeFilter;
import com.homescout.filter.FilterCriteria;
import com.homescout.filter.PriceRangeFilter;
import com.homescout.filter.PropertyFilter;
import com.homescout.filter.TypeFilter;
import com.homescout.model.Address;
import com.homescout.model.Apartment;
import com.homescout.model.Land;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.Villa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelAndFilterTest {
    private final Property flat = new Apartment("Flat", new Address("A St", "Jaipur"), 4_000_000, 1000, 2);
    private final Property villa = new Villa("Villa", new Address("B St", "Udaipur"), 20_000_000, 3000, 4, 3, true);
    private final Property plot = new Land("Plot", new Address("C St", "Jaipur"), 3_000_000, 2000, "Plot", true);

    ModelAndFilterTest() {
        flat.addAmenities("Lift", "Parking");
        villa.addAmenities("Pool");
    }

    // ---------- model ----------
    @Test
    void negativePriceIsRejected() {                                   // encapsulation
        assertThrows(InvalidPriceException.class,
                () -> new Villa("Bad", new Address("X", "Y"), -1, 1000, 3, 2, false));
    }

    @Test
    void blankTitleIsRejected() {
        assertThrows(InvalidInputException.class,
                () -> new Apartment("  ", new Address("X", "Y"), 1_000_000, 900, 2));
    }

    @Test
    void settersValidateToo() {
        assertThrows(InvalidPriceException.class, () -> flat.setPrice(5));
        flat.setPrice(2_000_000);
        assertEquals(2_000_000, flat.getPrice(), 0.001);
    }

    @Test
    void taxDependsOnTheRealSubclass() {                               // runtime polymorphism
        assertEquals(200_000, flat.calculateTax(), 0.001);             // apartment 5%
        assertEquals(1_600_000, villa.calculateTax(), 0.001);          // villa 8%
        assertEquals(120_000, plot.calculateTax(), 0.001);             // land 4%
    }

    @Test
    void comparableOrdersByPrice() {
        assertTrue(plot.compareTo(flat) < 0);
        assertTrue(villa.compareTo(flat) > 0);
    }

    @Test
    void equalityIsBasedOnId() {
        Property twin = new Apartment("Flat", new Address("A St", "Jaipur"), 4_000_000, 1000, 2);
        assertTrue(flat.equals(flat));
        assertFalse(flat.equals(twin));
    }

    @Test
    void varargsAmenitiesIgnoreDuplicates() {
        flat.addAmenities("Gym", "gym", "LIFT");
        assertEquals(3, flat.getAmenities().size());
        assertTrue(flat.hasAmenity("GYM"));
    }

    // ---------- filters ----------
    @Test
    void cityFilterIgnoresCase() {
        assertTrue(new CityFilter("jaipur").matches(flat));
        assertFalse(new CityFilter("jaipur").matches(villa));
    }

    @Test
    void priceRangeIncludesBounds() {
        PropertyFilter f = new PriceRangeFilter(4_000_000, 20_000_000);
        assertTrue(f.matches(flat));
        assertTrue(f.matches(villa));
        assertFalse(f.matches(plot));
    }

    @Test
    void bedroomFilterNeverMatchesLand() {
        assertTrue(new BedroomFilter(1).matches(flat));
        assertFalse(new BedroomFilter(1).matches(plot));
    }

    @Test
    void amenityFilterNeedsAllAmenities() {
        assertTrue(new AmenityFilter("Lift", "Parking").matches(flat));
        assertFalse(new AmenityFilter("Lift", "Pool").matches(flat));
    }

    @Test
    void compositeAndNeedsEverything() {
        CompositeFilter and = new CompositeFilter().add(new CityFilter("Jaipur")).add(new TypeFilter(PropertyType.APARTMENT));
        assertTrue(and.matches(flat));
        assertFalse(and.matches(plot));
    }

    @Test
    void compositeOrNeedsOnlyOne() {
        CompositeFilter or = new CompositeFilter(CompositeFilter.Mode.OR)
                .add(new TypeFilter(PropertyType.VILLA)).add(new TypeFilter(PropertyType.LAND));
        assertTrue(or.matches(villa));
        assertTrue(or.matches(plot));
        assertFalse(or.matches(flat));
    }

    @Test
    void emptyCompositeMatchesEverything() {
        assertTrue(new CompositeFilter().matches(flat));
    }

    @Test
    void defaultMethodsCombineFilters() {
        PropertyFilter cheap = p -> p.getPrice() < 5_000_000;
        assertTrue(cheap.and(new CityFilter("Jaipur")).matches(flat));
        assertTrue(cheap.negate().matches(villa));
        assertTrue(PropertyFilter.acceptAll().matches(plot));
    }

    @Test
    void builderSkipsBlankValues() {
        assertEquals(0, FilterCriteria.builder().city("  ").keyword("").minBedrooms(0).build().size());
        assertEquals(2, FilterCriteria.builder().city("Jaipur").minBedrooms(2).build().size());
    }
}