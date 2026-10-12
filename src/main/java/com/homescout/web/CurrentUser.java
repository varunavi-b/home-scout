package com.homescout.web;

import com.homescout.model.User;
import com.homescout.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/** Turns the browser session into the real User object (Buyer, Agent or Admin). */
@Component
public class CurrentUser {
    private final UserService users;

    public CurrentUser(UserService users) {
        this.users = users;
    }

    /** Returns null when nobody is logged in. */
    public User from(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(SessionUser.KEY);
        return (sessionUser == null) ? null : users.login(sessionUser.getEmail());
    }
}
