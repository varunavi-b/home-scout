package com.homescout;

import com.homescout.model.Agent;
import com.homescout.model.Buyer;
import com.homescout.model.Property;
import com.homescout.service.Catalog;
import com.homescout.service.DataStore;
import com.homescout.service.EnquiryService;
import com.homescout.service.UserService;
import com.homescout.util.SampleData;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataStoreTest {

    private Catalog<Property> sampleCatalog() {
        Catalog<Property> catalog = new Catalog<>();
        SampleData.load(catalog, new Agent("D One", "d1@test.com", 0.02), new Agent("D Two", "d2@test.com", 0.02));
        return catalog;
    }

    @Test
    void missingFilesMeanNothingSavedYet() throws Exception {
        Path dir = Files.createTempDirectory("store");
        DataStore store = new DataStore(dir);
        assertFalse(store.hasSavedProperties());
        assertTrue(store.loadProperties().isEmpty());
    }

    @Test
    void propertiesSurviveASaveAndLoad() throws Exception {
        Path dir = Files.createTempDirectory("store");
        DataStore store = new DataStore(dir);
        Catalog<Property> original = sampleCatalog();
        store.saveProperties(original.getAll());
        assertTrue(store.hasSavedProperties());

        List<Property> loaded = store.loadProperties();
        assertEquals(12, loaded.size());
        Property a = original.getAll().get(0);
        Property b = loaded.get(0);
        assertEquals(a.getId(), b.getId());
        assertEquals(a.getTitle(), b.getTitle());
        assertEquals(a.getPrice(), b.getPrice(), 0.001);
        assertEquals(a.getClass(), b.getClass());                     // still an Apartment, not just a Property
        assertEquals(a.getAmenities(), b.getAmenities());
        assertEquals(a.getAgentId(), b.getAgentId());
    }

    @Test
    void favouritesAndEnquiriesComeBack() throws Exception {
        Path dir = Files.createTempDirectory("store");
        DataStore store = new DataStore(dir);
        Catalog<Property> catalog = sampleCatalog();
        Property first = catalog.getAll().get(0);
        Property second = catalog.getAll().get(1);

        UserService users = new UserService();
        Buyer buyer = new Buyer("Save Buyer", "sb@mail.com", 5_000_000);
        users.register(buyer);
        buyer.addFavourite(first);
        buyer.addFavourite(second);
        EnquiryService enquiries = new EnquiryService();
        String tricky = "Hi, \"you\" | free?\nthanks & bye";
        enquiries.send(buyer, first, tricky);
        store.saveExtras(users, enquiries);

        UserService users2 = new UserService();
        Buyer buyer2 = new Buyer("Save Buyer", "sb@mail.com", 5_000_000);
        users2.register(buyer2);
        EnquiryService enquiries2 = new EnquiryService();
        store.loadExtras(users2, enquiries2, catalog);

        assertEquals(2, buyer2.getFavourites().size());
        assertTrue(buyer2.getFavourites().get(0) == first);           // the very same object as in the catalog
        assertEquals(1, enquiries2.all().size());
        assertEquals(tricky, enquiries2.all().get(0).getMessage());
    }

    @Test
    void linesForDeletedPropertiesAreSkipped() throws Exception {
        Path dir = Files.createTempDirectory("store");
        DataStore store = new DataStore(dir);
        Catalog<Property> catalog = sampleCatalog();
        Property first = catalog.getAll().get(0);

        UserService users = new UserService();
        Buyer buyer = new Buyer("Skip Buyer", "skip@mail.com", 5_000_000);
        users.register(buyer);
        buyer.addFavourite(first);
        EnquiryService enquiries = new EnquiryService();
        enquiries.send(buyer, first, "hello");
        store.saveExtras(users, enquiries);

        catalog.remove(first.getId());                                // the listing is deleted before the restart
        UserService users2 = new UserService();
        Buyer buyer2 = new Buyer("Skip Buyer", "skip@mail.com", 5_000_000);
        users2.register(buyer2);
        EnquiryService enquiries2 = new EnquiryService();
        store.loadExtras(users2, enquiries2, catalog);

        assertTrue(buyer2.getFavourites().isEmpty());
        assertTrue(enquiries2.all().isEmpty());
    }
}