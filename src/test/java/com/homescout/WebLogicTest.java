package com.homescout;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Address;
import com.homescout.model.Agent;
import com.homescout.model.Apartment;
import com.homescout.model.Buyer;
import com.homescout.model.FurnishingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.Shop;
import com.homescout.model.User;
import com.homescout.service.AuthService;
import com.homescout.service.Catalog;
import com.homescout.service.EnquiryService;
import com.homescout.service.SearchService;
import com.homescout.service.UserService;
import com.homescout.util.SampleData;
import com.homescout.web.EmiResult;
import com.homescout.web.FilterForm;
import com.homescout.web.PropertyForm;
import com.homescout.web.Redirects;
import com.homescout.web.StatsView;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebLogicTest {

    private static PropertyForm validApartment() {
        PropertyForm f = new PropertyForm();
        f.setType("Apartment");
        f.setTitle("New Flat");
        f.setStreet("Ring Road");
        f.setCity("Kota");
        f.setPincode("324001");
        f.setPriceLakh(55.0);
        f.setArea(1200.0);
        f.setBedrooms(3);
        f.setBathrooms(2);
        f.setFloor(5);
        f.setLift(true);
        f.setFurnishing("Semi");
        f.setAmenities("Lift, Gym ,Parking");
        return f;
    }

    // ---------- login ----------
    private AuthService authWithOneBuyer() {
        UserService users = new UserService();
        users.register(new Buyer("Test Buyer", "tb@mail.com", 5_000_000));
        AuthService auth = new AuthService(users);
        auth.setPassword("tb@mail.com", "secret1");
        return auth;
    }

    @Test
    void loginAcceptsTheRightPassword() {
        User u = authWithOneBuyer().login("  TB@mail.com ", "secret1");
        assertEquals("Buyer", u.getRole());
    }

    @Test
    void loginRejectsWrongPasswordAndUnknownEmail() {
        AuthService auth = authWithOneBuyer();
        assertThrows(InvalidInputException.class, () -> auth.login("tb@mail.com", "wrong"));
        assertThrows(InvalidInputException.class, () -> auth.login("nobody@mail.com", "secret1"));
        assertThrows(InvalidInputException.class, () -> auth.login("tb@mail.com", null));
    }

    // ---------- listing form ----------
    @Test
    void blankFormReportsTheMissingFields() {
        Map<String, String> errors = new PropertyForm().validateForCreate();
        assertTrue(errors.containsKey("title"));
        assertTrue(errors.containsKey("pincode"));
        assertTrue(errors.containsKey("priceLakh"));
        assertTrue(errors.containsKey("floor"));
    }

    @Test
    void validFormBuildsTheRightSubclass() {
        PropertyForm f = validApartment();
        assertTrue(f.validateForCreate().isEmpty());
        Property p = f.toProperty();
        assertTrue(p instanceof Apartment);
        assertEquals(5_500_000, p.getPrice(), 0.001);
        assertEquals(FurnishingStatus.SEMI_FURNISHED, p.getFurnishing());
        assertEquals(3, p.getAmenities().size());
    }

    @Test
    void everyTypeCanBeBuiltFromTheForm() {
        for (String type : List.of("House", "Villa", "Office", "Shop", "Land")) {
            PropertyForm f = validApartment();
            f.setType(type);
            f.setParking(2);
            f.setPlotType("Commercial plot");
            assertTrue(f.validateForCreate().isEmpty());
            assertEquals(PropertyType.fromText(type), f.toProperty().getType());
        }
    }

    @Test
    void formRulesAreEnforced() {
        PropertyForm f = validApartment();
        f.setPincode("12345");
        assertTrue(f.validateForCreate().containsKey("pincode"));
        f = validApartment();
        f.setPriceLakh(0.01);
        assertTrue(f.validateForCreate().containsKey("priceLakh"));
        f = validApartment();
        f.setBedrooms(25);
        assertTrue(f.validateForCreate().containsKey("bedrooms"));
        f = validApartment();
        f.setType("Land");
        f.setPlotType(" ");
        assertTrue(f.validateForCreate().containsKey("plotType"));
    }

    @Test
    void editFormPrefillsAndAppliesChanges() {
        Property shop = new Shop("Corner Shop", new Address("Main", "Ajmer"), 3_000_000, 300, 1, true);
        PropertyForm f = PropertyForm.from(shop);
        assertEquals("Corner Shop", f.getTitle());
        assertEquals(30.0, f.getPriceLakh(), 0.001);
        assertEquals(1, (int) f.getParking());
        f.setPriceLakh(32.5);
        f.setParking(4);
        assertTrue(f.validateForEdit(shop).isEmpty());
        f.applyTo(shop);
        assertEquals(3_250_000, shop.getPrice(), 0.001);
        f.setPriceLakh(0.0);
        assertTrue(f.validateForEdit(shop).containsKey("priceLakh"));
    }

    // ---------- small helpers ----------
    @Test
    void redirectsStayInsideTheSite() {
        assertEquals("/listings?page=2", Redirects.safe("/listings?page=2", "/x"));
        assertEquals("/x", Redirects.safe("//evil.com", "/x"));
        assertEquals("/x", Redirects.safe("http://evil.com", "/x"));
        assertEquals("/x", Redirects.safe(null, "/x"));
    }

    @Test
    void filterFormBuildsQueryAndCriteria() {
        FilterForm f = new FilterForm();
        f.setCity("Jaipur");
        f.setTypes(List.of("Villa"));
        f.setKeyword("lake view");
        String q = f.toQueryString();
        assertTrue(q.contains("city=Jaipur&"));
        assertTrue(q.contains("types=Villa&"));
        assertTrue(q.contains("keyword=lake+view&"));
        assertEquals("city = Jaipur AND type = Villa AND keyword = lake view", f.toCriteria().describe());
    }

    @Test
    void emiResultChecksItsInput() {
        Property p = new Apartment("Flat", new Address("A", "B"), 4_500_000, 1000, 2);
        assertEquals("Rs 31,242", EmiResult.calculate(p, 8.5, 20, 20).getMonthlyEmi());
        assertThrows(InvalidInputException.class, () -> EmiResult.calculate(p, 8.5, 45, 20));
        assertThrows(InvalidInputException.class, () -> EmiResult.calculate(p, 8.5, 20, 95));
    }

    // ---------- statistics ----------
    @Test
    void statisticsMatchTheSampleData() {
        Catalog<Property> catalog = new Catalog<>();
        SampleData.load(catalog, new Agent("A One", "s1@test.com", 0.02), new Agent("A Two", "s2@test.com", 0.02));
        StatsView s = StatsView.build(new SearchService(catalog), catalog, new UserService(), new EnquiryService());
        assertEquals(12, s.getTotalListings());
        assertEquals(10, s.getAvailable());
        assertEquals(1, s.getSold());
        assertEquals(1, s.getRented());
        assertTrue(s.getMostExpensive().startsWith("Palm Grove Villa"));
        assertTrue(s.getCheapest().startsWith("Green Park 2A"));
        assertEquals(4, s.getPriceByCity().size());
        assertFalse(s.getCountByType().isEmpty());
    }
}