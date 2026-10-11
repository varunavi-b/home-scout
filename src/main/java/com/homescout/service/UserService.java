package com.homescout.service;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<String, User> byEmail = new LinkedHashMap<>();

    public void register(User user) {
        if (byEmail.containsKey(user.getEmail())) {
            throw new InvalidInputException("Email already registered: " + user.getEmail());
        }
        byEmail.put(user.getEmail(), user);
    }

    public User login(String email) {
        User user = (email == null) ? null : byEmail.get(email.trim().toLowerCase());
        if (user == null) throw new InvalidInputException("No account found for: " + email);
        return user;
    }

    public Collection<User> getAll() { return new ArrayList<>(byEmail.values()); }

    /** Generic helper: returns only users of one subclass, e.g. getAllOfType(Agent.class). */
    public <U extends User> List<U> getAllOfType(Class<U> type) {
        List<U> result = new ArrayList<>();
        for (User u : byEmail.values()) {
            if (type.isInstance(u)) result.add(type.cast(u));
        }
        return result;
    }
}
