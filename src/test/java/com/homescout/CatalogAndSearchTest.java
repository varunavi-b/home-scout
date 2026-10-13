package com.homescout;

import com.homescout.exception.DuplicatePropertyException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.filter.PropertyFilter;
import com.homescout.model.Agent;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.service.Catalog;
import com.homescout.service.SearchService;
import com.homescout.sort.PriceAscendingSort;
import com.homescout.sort.PriceDescendingSort;
import com.homescout.util.SampleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogAndSearchTest {
    private Catalog<Property> catalog;
    private SearchService search;

    @BeforeEach
    void setUp() {
        catalog = new Catalog<>();
        SampleData.load(catalog, new Agent("A One", "a1@test.com", 0.02), new Agent("A Two", "a2@test.com", 0.02));
        search = new SearchService(catalog);
    }

    @Test
    void sampleDataHasTwelveListings() {
        assertEquals(12, catalog.size());
    }

    @Test
    void findByIdReturnsTheProperty() throws PropertyNotFoundException {
        Property first = catalog.getAll().get(0);
        assertEquals(first, catalog.findById(first.getId()));
    }

    @Test
    void unknownIdThrowsCheckedException() {
        assertThrows(PropertyNotFoundException.class, () -> catalog.findById(-5));
        assertThrows(PropertyNotFoundException.class, () -> catalog.remove(-5));
    }

    @Test
    void duplicateAddThrows() {
        Property first = catalog.getAll().get(0);
        assertThrows(DuplicatePropertyException.class, () -> catalog.add(first));
    }

    @Test
    void removeShrinksCatalog() throws PropertyNotFoundException {
        Property first = catalog.getAll().get(0);
        catalog.remove(first.getId());
        assertEquals(11, catalog.size());
        assertFalse(catalog.contains(first.getId()));
    }

    @Test
    void getAllReturnsACopy() {
        catalog.getAll().clear();
        assertEquals(12, catalog.size());
    }

    @Test
    void overloadedSearchMethods() {
        assertEquals(5, search.search("Jaipur").size());
        assertEquals(6, search.search(3_000_000, 7_000_000).size());
        assertEquals(2, search.search(PropertyType.VILLA, 4).size());
    }

    @Test
    void sortStrategiesChangeOrder() {
        List<Property> asc = search.search(PropertyFilter.acceptAll(), new PriceAscendingSort());
        List<Property> desc = search.search(PropertyFilter.acceptAll(), new PriceDescendingSort());
        assertTrue(asc.get(0).getPrice() <= asc.get(1).getPrice());
        assertEquals(asc.get(0), desc.get(desc.size() - 1));
    }

    @Test
    void paginationSlicesResults() {
        List<Property> all = catalog.getAll();
        assertEquals(5, search.page(all, 1, 5).size());
        assertEquals(2, search.page(all, 3, 5).size());
        assertTrue(search.page(all, 9, 5).isEmpty());
    }

    @Test
    void statisticsAreComputed() {
        assertEquals(4, search.averagePriceByCity().size());
        assertEquals(3L, (long) search.countByType().get(PropertyType.APARTMENT));
    }

    @Test
    void recommendOnlyReturnsAvailableWithinBudget() {
        for (Property p : search.recommend(7_000_000, 10)) {
            assertTrue(p.getPrice() <= 7_000_000);
            assertTrue(p.getStatus().isOpen());
        }
    }
}