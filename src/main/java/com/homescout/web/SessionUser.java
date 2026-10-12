package com.homescout.web;

import com.homescout.model.User;

import java.io.Serializable;

/** A tiny copy of "who is logged in" kept in the browser session. */
public class SessionUser implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String KEY = "sessionUser";

    private final String email;
    private final String name;
    private final String role;

    public SessionUser(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();        // runtime polymorphism: "Buyer", "Agent" or "Admin"
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public boolean isBuyer() { return "Buyer".equals(role); }
    public boolean isAgent() { return "Agent".equals(role); }
    public boolean isAdmin() { return "Admin".equals(role); }
}
