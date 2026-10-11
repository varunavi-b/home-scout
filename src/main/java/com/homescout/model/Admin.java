package com.homescout.model;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin(String name, String email) {
        super(name, email);
    }

    @Override
    public String getRole() { return "Admin"; }

    @Override
    public boolean canManage(Property property) { return true; }    // admins manage everything
}
