package com.homescout.controller;     // <-- CHANGE THIS to your package name + .controller

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "HomeScout");
        model.addAttribute("tagline", "Search properties using many filters at once");
        return "index";
    }
}