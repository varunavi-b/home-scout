package com.homescout.model;

import com.homescout.exception.InvalidInputException;

import java.io.Serializable;
import java.util.Objects;

/**
 * ENCAPSULATION + IMMUTABILITY: final class, final private fields, no setters.
 * A Property HAS-A Address (COMPOSITION).
 */
public final class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String street;
    private final String city;
    private final String state;
    private final String pincode;

    public Address(String street, String city, String state, String pincode) {
        this.street = require(street, "Street");
        this.city = require(city, "City");
        this.state = require(state, "State");
        this.pincode = require(pincode, "Pincode");
    }

    /** OVERLOADED constructor (compile-time polymorphism) using defaults via this(...). */
    public Address(String street, String city) {
        this(street, city, "Rajasthan", "000000");
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(field + " must not be empty");
        }
        return value.trim();
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }

    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " - " + pincode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address a = (Address) o;
        return street.equalsIgnoreCase(a.street) && city.equalsIgnoreCase(a.city)
                && state.equalsIgnoreCase(a.state) && pincode.equals(a.pincode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street.toLowerCase(), city.toLowerCase(), state.toLowerCase(), pincode);
    }
}
