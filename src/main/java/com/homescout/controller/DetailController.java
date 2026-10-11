package com.homescout.controller;

import com.homescout.exception.InvalidInputException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.model.Property;
import com.homescout.service.PropertyCatalog;
import com.homescout.web.EmiResult;
import com.homescout.web.PropertyDetail;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class DetailController {

    private final PropertyCatalog catalog;

    public DetailController(PropertyCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/listings/{id}")
    public String detail(@PathVariable int id,
                         @RequestParam(defaultValue = "20") int down,
                         @RequestParam(defaultValue = "8.5") double rate,
                         @RequestParam(defaultValue = "20") int years,
                         Model model) {
        Property property;
        try {
            property = catalog.findById(id);                      // throws your checked exception
        } catch (PropertyNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        model.addAttribute("p", PropertyDetail.from(property));
        model.addAttribute("down", down);
        model.addAttribute("rate", rate);
        model.addAttribute("years", years);

        try {
            model.addAttribute("emi", EmiResult.calculate(property, rate, years, down));
        } catch (InvalidInputException e) {
            model.addAttribute("emiError", e.getMessage());
        }
        return "property";
    }
}
