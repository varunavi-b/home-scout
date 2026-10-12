package com.homescout.controller;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Buyer;
import com.homescout.model.User;
import com.homescout.service.AuthService;
import com.homescout.web.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        try {
            User user = auth.login(email, password);
            HttpSession session = request.getSession();
            request.changeSessionId();                       // fresh session id after login (safer)
            session.setAttribute(SessionUser.KEY, new SessionUser(user));
            return (user instanceof Buyer) ? "redirect:/listings" : "redirect:/manage";
        } catch (InvalidInputException e) {
            model.addAttribute("loginError", e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
