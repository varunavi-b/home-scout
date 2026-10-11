package com.homescout.model;

import com.homescout.exception.InvalidInputException;

import java.io.Serializable;

/** ABSTRACT base class for all roles. */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 0;

    private final int id;
    private final String name;
    private final String email;

    protected User(String name, String email) {
        if (name == null || name.isBlank()) throw new InvalidInputException("Name must not be empty");
        if (email == null || !email.contains("@")) throw new InvalidInputException("Invalid email: " + email);
        this.id = ++counter;
        this.name = name.trim();
        this.email = email.trim().toLowerCase();
    }

    public abstract String getRole();                         // each role names itself

    public abstract boolean canManage(Property property);     // each role has its own permission rule

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() { return name + " (" + getRole() + ", " + email + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return email.equals(((User) o).email);
    }

    @Override
    public int hashCode() { return email.hashCode(); }
}
