package com.homescout.web;

import com.homescout.exception.DuplicatePropertyException;
import com.homescout.exception.InvalidInputException;
import com.homescout.exception.InvalidPriceException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.filter.CityFilter;
import com.homescout.filter.CompositeFilter;
import com.homescout.filter.FilterCriteria;
import com.homescout.filter.PropertyFilter;
import com.homescout.filter.TypeFilter;
import com.homescout.model.Address;
import com.homescout.model.Agent;
import com.homescout.model.Apartment;
import com.homescout.model.Commercial;
import com.homescout.model.Land;
import com.homescout.model.ListingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.Residential;
import com.homescout.model.User;
import com.homescout.model.Villa;
import com.homescout.service.Catalog;
import com.homescout.service.EnquiryService;
import com.homescout.service.PropertyCatalog;
import com.homescout.service.SearchService;
import com.homescout.service.UserService;
import com.homescout.sort.AreaDescendingSort;
import com.homescout.sort.PriceAscendingSort;
import com.homescout.sort.PriceDescendingSort;
import com.homescout.sort.SortStrategy;
import com.homescout.util.EmiCalculator;
import com.homescout.util.FileHandler;
import com.homescout.util.Money;
import com.homescout.util.PropertyFactory;
import com.homescout.util.Validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Runs small, harmless experiments on the LIVE data and reports what happened, one section per OOP concept.
 * It only reads: it never changes a listing and never creates a new one.
 */
public class ConceptsReport {

    /** STATIC NESTED CLASS: one numbered block on the page. */
    public static class Section {
        private final int number;
        private final String title;
        private final String where;
        private final List<String> lines = new ArrayList<>();

        Section(int number, String title, String where) {
            this.number = number;
            this.title = title;
            this.where = where;
        }

        void add(String line) { lines.add(line); }

        public int getNumber() { return number; }
        public String getTitle() { return title; }
        public String getWhere() { return where; }
        public List<String> getLines() { return lines; }
    }

    private final PropertyCatalog catalog;
    private final SearchService search;
    private final UserService users;
    private final EnquiryService enquiries;
    private final List<Property> all;
    private final List<Section> sections = new ArrayList<>();

    private ConceptsReport(PropertyCatalog catalog, SearchService search, UserService users, EnquiryService enquiries) {
        this.catalog = catalog;
        this.search = search;
        this.users = users;
        this.enquiries = enquiries;
        this.all = catalog.getAll();
    }

    public static List<Section> build(PropertyCatalog catalog, SearchService search,
                                      UserService users, EnquiryService enquiries) {
        ConceptsReport r = new ConceptsReport(catalog, search, users, enquiries);
        if (r.all.isEmpty()) {
            Section s = r.next("No data", "-");
            s.add("The catalog is empty. Add a listing first, then reload this page.");
            return r.sections;
        }
        r.encapsulation();
        r.inheritance();
        r.compileTimePolymorphism();
        r.runtimePolymorphism();
        r.abstraction();
        r.interfaces();
        r.thisAndSuper();
        r.staticAndFinal();
        r.relationships();
        r.enums();
        r.exceptions();
        r.generics();
        r.nestedAndAnonymous();
        r.lambdasAndStreams();
        r.objectMethods();
        r.instanceofAndCasting();
        r.designPatterns();
        r.serialization();
        r.packages();
        return r.sections;
    }

    private Section next(String title, String where) {
        Section s = new Section(sections.size() + 1, title, where);
        sections.add(s);
        return s;
    }

    private Property firstOf(Class<? extends Property> type) {
        return all.stream().filter(type::isInstance).findFirst().orElse(all.get(0));
    }

    private int count(PropertyFilter f) {
        return (int) all.stream().filter(f::matches).count();
    }

    // 1 ------------------------------------------------------------------
    private void encapsulation() {
        Section s = next("Encapsulation", "model/Property.java, model/Address.java");
        Property p = all.get(0);
        s.add("price is a private field, read through getPrice(): " + Money.format(p.getPrice()));
        try {
            p.setPrice(-500);
        } catch (InvalidPriceException e) {
            s.add("setPrice(-500) was rejected: " + e.getMessage());
        }
        s.add("Address has only final fields and no setters: " + p.getAddress());
        try {
            p.getAmenities().add("Hacked");
        } catch (UnsupportedOperationException e) {
            s.add("getAmenities() returns a read-only list, so outside code cannot change the internal list");
        }
    }

    // 2 ------------------------------------------------------------------
    private void inheritance() {
        Section s = next("Inheritance (single, multilevel, hierarchical)", "model/Property.java, Residential.java, Villa.java, Land.java");
        StringBuilder chain = new StringBuilder();
        for (Class<?> c = firstOf(Villa.class).getClass(); c != null; c = c.getSuperclass()) {
            chain.append(c.getSimpleName());
            if (c.getSuperclass() != null) chain.append(" -> ");
        }
        s.add("Multilevel: " + chain);
        s.add("Single: Land extends " + Land.class.getSuperclass().getSimpleName());
        s.add("Hierarchical: Residential, Commercial and Land all extend Property");
    }

    // 3 ------------------------------------------------------------------
    private void compileTimePolymorphism() {
        Section s = next("Compile-time polymorphism (overloading and varargs)", "service/SearchService.java, util/EmiCalculator.java, model/Address.java");
        s.add("search(String city)                -> " + search.search("Jaipur").size() + " results");
        s.add("search(double min, double max)     -> " + search.search(3_000_000, 7_000_000).size() + " results");
        s.add("search(PropertyType, int bedrooms) -> " + search.search(PropertyType.VILLA, 4).size() + " results");
        s.add("Address(street, city) and Address(street, city, state, pincode) -> "
                + new Address("Main Road", "Kota") + "  |  " + new Address("Main Road", "Kota", "Rajasthan", "324001"));
        s.add("EmiCalculator.emi(principal)               -> " + Money.format(EmiCalculator.emi(4_000_000)));
        s.add("EmiCalculator.emi(principal, 7.0, 15)      -> " + Money.format(EmiCalculator.emi(4_000_000, 7.0, 15)));
        s.add("Varargs: new TypeFilter(VILLA, LAND) matches " + count(new TypeFilter(PropertyType.VILLA, PropertyType.LAND)) + " listings");
        s.add("Varargs: builder().amenities(\"Pool\", \"Gym\") -> " + FilterCriteria.builder().amenities("Pool", "Gym").describe());
    }

    // 4 ------------------------------------------------------------------
    private void runtimePolymorphism() {
        Section s = next("Runtime polymorphism (overriding)", "model/Apartment.java, Villa.java, Land.java ... calculateTax()");
        s.add("Every variable below has the type Property, but Java runs the version from the real class:");
        int shown = 0;
        for (Property p : all) {
            if (shown++ == 12) break;
            s.add(String.format("  #%d %-10s calculateTax() = %-12s (from class %s)",
                    p.getId(), p.getType().getLabel(), Money.format(p.calculateTax()), p.getClass().getSimpleName()));
        }
        s.add("getDetails() is overridden at every level of the hierarchy:");
        s.add("  " + firstOf(Villa.class).getDetails());
        s.add("  " + firstOf(Land.class).getDetails());
    }

    // 5 ------------------------------------------------------------------
    private void abstraction() {
        Section s = next("Abstraction (abstract classes and methods)", "model/Property.java, Residential.java, Commercial.java, User.java");
        s.add("Property is abstract: " + Modifier.isAbstract(Property.class.getModifiers()) + "  (new Property(...) will not compile)");
        s.add("Residential is abstract: " + Modifier.isAbstract(Residential.class.getModifiers()));
        s.add("Commercial is abstract: " + Modifier.isAbstract(Commercial.class.getModifiers()));
        s.add("User is abstract: " + Modifier.isAbstract(User.class.getModifiers()));
        Property p = all.get(0);
        s.add("The abstract method canManage(...) gives a different answer per role, for #" + p.getId() + ":");
        for (User u : users.getAll()) {
            s.add("  " + u.getRole() + " " + u.getName() + " -> " + u.canManage(p));
        }
    }

    // 6 ------------------------------------------------------------------
    private void interfaces() {
        Section s = next("Interfaces (abstract, default and static methods, lambdas)", "filter/PropertyFilter.java, model/Bookmarkable.java");
        PropertyFilter cheap = p -> p.getPrice() < 5_000_000;           // a lambda implements the interface
        PropertyFilter inJaipur = new CityFilter("Jaipur");
        s.add("cheap (lambda: price under Rs 50 L) -> " + count(cheap));
        s.add("cheap.and(inJaipur)   default method -> " + count(cheap.and(inJaipur)));
        s.add("cheap.or(inJaipur)    default method -> " + count(cheap.or(inJaipur)));
        s.add("cheap.negate()        default method -> " + count(cheap.negate()));
        s.add("PropertyFilter.acceptAll()  static method -> " + count(PropertyFilter.acceptAll()));
        s.add("Bookmarkable default method bookmarkLabel() -> " + all.get(0).bookmarkLabel());
        s.add("Property implements: " + Arrays.stream(Property.class.getInterfaces())
                .map(Class::getSimpleName).collect(Collectors.joining(", ")) + "  (several interfaces at once)");
    }

    // 7 ------------------------------------------------------------------
    private void thisAndSuper() {
        Section s = next("this and super (constructor chaining)", "model/Apartment.java, Residential.java, Property.java");
        List<Integer> sizes = new ArrayList<>();
        for (Constructor<?> c : Apartment.class.getConstructors()) sizes.add(c.getParameterCount());
        Collections.sort(sizes, Collections.reverseOrder());
        s.add("Apartment has public constructors with these parameter counts: " + sizes);
        s.add("The short one calls this(...) to reach the long one.");
        s.add("The long one calls super(...), which calls Residential's constructor, which calls Property's constructor.");
    }

    // 8 ------------------------------------------------------------------
    private void staticAndFinal() {
        Section s = next("static and final", "model/Property.java, util/Money.java");
        s.add("static counter: the next property id will be " + Property.peekNextId() + " (one counter shared by all objects)");
        s.add("final constant: Property.MIN_PRICE = " + Money.format(Property.MIN_PRICE));
        s.add("static utility classes with private constructors: Money, Validator, EmiCalculator, FileHandler, PropertyFactory");
    }

    // 9 ------------------------------------------------------------------
    private void relationships() {
        Section s = next("Composition, aggregation, association", "model/Property.java, Agent.java, Buyer.java, Enquiry.java");
        s.add("Composition: a Property HAS-A Address -> " + all.get(0).getAddress().getCity() + " (an address never exists on its own)");
        for (Agent agent : users.getAllOfType(Agent.class)) {
            s.add("Aggregation: agent " + agent.getName() + " keeps a list of " + agent.getListings().size() + " listings");
        }
        if (enquiries.all().isEmpty()) {
            s.add("Association: no enquiries yet. Log in as a buyer and send one, then reload this page.");
        } else {
            s.add("Association (Buyer - Property): " + enquiries.all().get(0));
        }
    }

    // 10 -----------------------------------------------------------------
    private void enums() {
        Section s = next("Enums with fields and methods", "model/PropertyType.java, ListingStatus.java, FurnishingStatus.java");
        for (PropertyType t : PropertyType.values()) {
            s.add(String.format("  %-10s label=%-10s residential=%s", t.name(), t.getLabel(), t.isResidential()));
        }
        for (ListingStatus st : ListingStatus.values()) {
            s.add("  " + st.getLabel() + " -> isOpen() = " + st.isOpen());
        }
    }

    // 11 -----------------------------------------------------------------
    private void exceptions() {
        Section s = next("Exception handling (custom, checked and unchecked, finally)", "exception/*.java, service/Catalog.java");
        try {
            catalog.findById(99999);
        } catch (PropertyNotFoundException e) {
            s.add("Checked exception   : " + e.getMessage());
        } finally {
            s.add("finally block       : this line is always added, even after an exception");
        }
        try {
            catalog.add(all.get(0));
        } catch (DuplicatePropertyException e) {
            s.add("Unchecked exception : " + e.getMessage());
        }
        try {
            Validator.parsePositiveDouble("abc", "Price");
        } catch (InvalidInputException e) {
            s.add("Validation          : " + e.getMessage());
        }
        try {
            PropertyType.fromText("castle");
        } catch (InvalidInputException | IllegalStateException e) {
            s.add("Multi-catch         : " + e.getMessage());
        }
    }

    // 12 -----------------------------------------------------------------
    private static <T extends Property> T mostExpensive(List<T> list) {       // a generic method
        return Collections.max(list);
    }

    private void generics() {
        Section s = next("Generics and collections", "service/Catalog.java, service/UserService.java");
        Catalog<Villa> villas = new Catalog<>();
        for (Property p : all) {
            if (p instanceof Villa v) villas.add(v);
        }
        s.add("Catalog<Villa> holds " + villas.size() + " villas, while Catalog<Property> holds " + catalog.size() + " properties");
        s.add("Generic method mostExpensive(List<T>) -> " + mostExpensive(all));
        s.add("UserService.getAllOfType(Agent.class) -> " + users.getAllOfType(Agent.class).size() + " agents");
        s.add("Map<PropertyType, Long> countByType() -> " + search.countByType());
    }

    // 13 -----------------------------------------------------------------
    private void nestedAndAnonymous() {
        Section s = next("Nested, inner and anonymous classes", "filter/FilterCriteria.java, service/SearchService.java");
        FilterCriteria.Builder builder = FilterCriteria.builder().city("Jaipur").minBedrooms(3);
        s.add("Static nested class FilterCriteria.Builder -> " + builder.describe() + " -> " + count(builder.build()) + " results");
        PropertyFilter big = new PropertyFilter() {                            // anonymous class
            @Override
            public boolean matches(Property p) {
                return p.getArea() > 3000;
            }
        };
        s.add("Anonymous PropertyFilter (area over 3000 sq ft) -> " + count(big) + " results");
        s.add("Anonymous Comparator in sortByCityThenPrice, first three:");
        search.sortByCityThenPrice(all).stream().limit(3).forEach(p -> s.add("   " + p));
    }

    // 14 -----------------------------------------------------------------
    private void lambdasAndStreams() {
        Section s = next("Lambdas and streams", "service/SearchService.java");
        for (Map.Entry<String, Double> e : search.averagePriceByCity().entrySet()) {
            s.add(String.format("Average price in %-8s = %s", e.getKey(), Money.format(e.getValue())));
        }
        s.add("recommend(budget Rs 70 L, 3) -> "
                + search.recommend(7_000_000, 3).stream().map(Property::getTitle).collect(Collectors.toList()));
    }

    // 15 -----------------------------------------------------------------
    private void objectMethods() {
        Section s = next("Object class methods (toString, equals, hashCode, compareTo)", "model/Property.java");
        if (all.size() < 2) {
            s.add("Add at least two listings to see this one.");
            return;
        }
        Property a = all.get(0);
        Property b = all.get(1);
        s.add("toString()  : " + a);
        s.add("equals()    : a.equals(a) = " + a.equals(a) + ", a.equals(b) = " + a.equals(b));
        s.add("hashCode()  : " + a.hashCode() + " (based on the id)");
        s.add("compareTo() : a vs b = " + a.compareTo(b) + " (negative means a is cheaper)");
    }

    // 16 -----------------------------------------------------------------
    private void instanceofAndCasting() {
        Section s = next("instanceof and casting", "web/PropertyDetail.java, filter/BedroomFilter.java");
        for (Property p : all.stream().limit(6).collect(Collectors.toList())) {
            String extra;
            if (p instanceof Villa v) extra = "Villa, pool = " + v.hasPool();
            else if (p instanceof Apartment ap) extra = "Apartment, floor = " + ap.getFloor();
            else if (p instanceof Land l) extra = "Land, plot type = " + l.getPlotType();
            else extra = p.getClass().getSimpleName() + ", no special handling";
            s.add("#" + p.getId() + " -> " + extra);
        }
    }

    // 17 -----------------------------------------------------------------
    private void designPatterns() {
        Section s = next("Design patterns", "service/PropertyCatalog.java, util/PropertyFactory.java, sort/*, filter/*");
        s.add("Singleton: same PropertyCatalog object every time? " + (PropertyCatalog.getInstance() == PropertyCatalog.getInstance()));
        s.add("Factory: PropertyFactory.create(type, ...) picks the class to build. The Add listing form uses it for: "
                + Arrays.stream(PropertyType.values()).map(PropertyType::getLabel).collect(Collectors.joining(", ")));
        s.add("Factory class: " + PropertyFactory.class.getSimpleName() + " (private constructor, static methods only)");
        for (SortStrategy st : new SortStrategy[]{new PriceAscendingSort(), new PriceDescendingSort(), new AreaDescendingSort()}) {
            s.add("Strategy: " + st.label() + " -> first = " + search.search(PropertyFilter.acceptAll(), st).get(0).getTitle());
        }
        CompositeFilter nested = new CompositeFilter(CompositeFilter.Mode.AND)
                .add(new CityFilter("Jaipur"))
                .add(new CompositeFilter(CompositeFilter.Mode.OR)
                        .add(new TypeFilter(PropertyType.VILLA))
                        .add(new TypeFilter(PropertyType.OFFICE)));
        s.add("Composite: Jaipur AND (Villa OR Office) -> " + count(nested) + " results");
        s.add("Builder: " + FilterCriteria.builder().city("Udaipur").onlyAvailable().describe());
    }

    // 18 -----------------------------------------------------------------
    private void serialization() {
        Section s = next("File I/O and serialization", "util/FileHandler.java, service/DataStore.java");
        try {
            Path temp = Files.createTempFile("concepts", ".dat");
            FileHandler.saveBinary(temp, all);
            List<Property> loaded = FileHandler.loadBinary(temp);
            s.add("Saved " + all.size() + " objects to a temporary file and loaded " + loaded.size() + " back.");
            s.add("First one: " + loaded.get(0).getTitle() + " (class " + loaded.get(0).getClass().getSimpleName() + " again)");
            Files.deleteIfExists(temp);
        } catch (Exception e) {
            s.add("I/O problem: " + e.getMessage());
        }
    }

    // 19 -----------------------------------------------------------------
    private void packages() {
        Section s = next("Packages", "the folder structure of the project");
        Class<?>[] samples = {Property.class, PropertyFilter.class, SearchService.class, SortStrategy.class,
                InvalidPriceException.class, Money.class, ConceptsReport.class};
        for (Class<?> c : samples) {
            s.add(String.format("%-24s lives in %s", c.getSimpleName(), c.getPackageName()));
        }
    }
}