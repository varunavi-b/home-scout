package com.homescout.controller;

import com.homescout.web.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Puts "who is logged in" into every page, so the menu bar can show the right links. */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionUser.KEY);
    }
}
