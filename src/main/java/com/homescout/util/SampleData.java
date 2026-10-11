package com.homescout.util;

import com.homescout.model.Address;
import com.homescout.model.Agent;
import com.homescout.model.Apartment;
import com.homescout.model.FurnishingStatus;
import com.homescout.model.House;
import com.homescout.model.Land;
import com.homescout.model.ListingStatus;
import com.homescout.model.Office;
import com.homescout.model.Property;
import com.homescout.model.Shop;
import com.homescout.model.Villa;
import com.homescout.service.Catalog;

/** Seeds the catalog with 12 demo listings (every concrete subclass appears at least once). */
public final class SampleData {

    private SampleData() { }

    public static void load(Catalog<Property> catalog, Agent a1, Agent a2) {
        add(catalog, a1, new Apartment("Sunrise Heights 4B", new Address("MI Road", "Jaipur", "Rajasthan", "302001"),
                4_500_000, 1100, 2, 2, 4, true), FurnishingStatus.SEMI_FURNISHED, "Lift", "Parking");

        add(catalog, a1, new Villa("Palm Grove Villa", new Address("Vaishali Nagar", "Jaipur", "Rajasthan", "302021"),
                21_000_000, 3200, 4, 4, true), FurnishingStatus.FURNISHED, "Pool", "Garden", "Parking");

        add(catalog, a2, new House("Old City Home", new Address("Clock Tower Road", "Jodhpur", "Rajasthan", "342001"),
                8_500_000, 1800, 3, 2, true), FurnishingStatus.UNFURNISHED, "Garden");

        add(catalog, a2, new Apartment("Lakeview Residency", new Address("Fateh Sagar Road", "Udaipur", "Rajasthan", "313001"),
                6_200_000, 1450, 3, 2, 7, true), FurnishingStatus.FURNISHED, "Lift", "Gym", "Parking");

        add(catalog, a1, new Office("Tech Park Unit 12", new Address("Mahapura", "Jaipur", "Rajasthan", "302026"),
                12_000_000, 900, 4, true), FurnishingStatus.SEMI_FURNISHED, "Lift", "Parking");

        Property shop = new Shop("Market Road Shop", new Address("Station Road", "Ajmer", "Rajasthan", "305001"),
                5_500_000, 400, 0, true);
        shop.setStatus(ListingStatus.RENTED);
        add(catalog, a2, shop, FurnishingStatus.UNFURNISHED);

        add(catalog, a1, new Land("Highway Plot", new Address("NH 62", "Jodhpur", "Rajasthan", "342005"),
                3_800_000, 2400, "Commercial plot", false), FurnishingStatus.UNFURNISHED);

        Property hilltop = new Villa("Hilltop Estate", new Address("Sajjangarh Road", "Udaipur", "Rajasthan", "313001"),
                18_000_000, 4000, 5, 5, true);
        hilltop.setStatus(ListingStatus.SOLD);
        add(catalog, a2, hilltop, FurnishingStatus.FURNISHED, "Pool", "Garden", "Gym");

        // uses the OVERLOADED 5-argument Apartment constructor
        add(catalog, a1, new Apartment("Green Park 2A", new Address("Pushkar Road", "Ajmer", "Rajasthan", "305001"),
                3_400_000, 900, 2), FurnishingStatus.UNFURNISHED);

        add(catalog, a1, new House("Garden Lane Cottage", new Address("Malviya Nagar", "Jaipur", "Rajasthan", "302017"),
                9_500_000, 2000, 3, 3, true), FurnishingStatus.SEMI_FURNISHED, "Garden", "Parking");

        add(catalog, a2, new Shop("Station Arcade Shop", new Address("Hiran Magri", "Udaipur", "Rajasthan", "313002"),
                7_200_000, 550, 2, true), FurnishingStatus.SEMI_FURNISHED);

        add(catalog, a2, new Land("Green Valley Plot", new Address("Ajmer Road", "Jaipur", "Rajasthan", "302026"),
                5_000_000, 3000, "Agricultural plot", true), FurnishingStatus.UNFURNISHED);
    }

    private static void add(Catalog<Property> catalog, Agent agent, Property p,
                            FurnishingStatus furnishing, String... amenities) {
        p.setFurnishing(furnishing);
        p.addAmenities(amenities);
        agent.addListing(p);          // aggregation: agent keeps a reference and becomes the owner
        catalog.add(p);
    }
}
