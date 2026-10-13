package com.homescout.controller;

import com.homescout.exception.InvalidInputException;
import com.homescout.exception.InvalidPriceException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.model.Agent;
import com.homescout.model.Buyer;
import com.homescout.model.Commercial;
import com.homescout.model.ListingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.Residential;
import com.homescout.model.User;
import com.homescout.service.PropertyCatalog;
import com.homescout.service.UserService;
import com.homescout.web.CurrentUser;
import com.homescout.web.PropertyCard;
import com.homescout.web.PropertyForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Agents manage their own listings; the admin can manage all of them. */
@Controller
public class ManageController {

    private final PropertyCatalog catalog;
    private final UserService users;
    private final CurrentUser currentUser;

    public ManageController(PropertyCatalog catalog, UserService users, CurrentUser currentUser) {
        this.catalog = catalog;
        this.users = users;
        this.currentUser = currentUser;
    }

    @ModelAttribute("typeOptions")
    public List<String> typeOptions() {
        return Arrays.stream(PropertyType.values()).map(PropertyType::getLabel).collect(Collectors.toList());
    }

    // ---------------- list ----------------
    @GetMapping("/manage")
    public String list(HttpSession session, Model model) {
        User user = currentUser.from(session);
        List<PropertyCard> rows = catalog.getAll().stream()
                .filter(user::canManage)            // POLYMORPHISM: Agent = own listings, Admin = everything
                .map(PropertyCard::from)
                .collect(Collectors.toList());
        model.addAttribute("rows", rows);
        model.addAttribute("heading", (user instanceof Agent) ? "My listings" : "All listings (admin)");
        return "manage";
    }

    // ---------------- add ----------------
    @GetMapping("/manage/new")
    public String newForm(HttpSession session, Model model) {
        requireAgent(session);
        model.addAttribute("form", new PropertyForm());
        model.addAttribute("errors", Map.of());
        model.addAttribute("editing", false);
        return "manage-form";
    }

    @PostMapping("/manage")
    public String create(@ModelAttribute("form") PropertyForm form,
                         HttpSession session, Model model, RedirectAttributes redirect) {
        Agent agent = requireAgent(session);
        Map<String, String> errors = form.validateForCreate();
        if (errors.isEmpty()) {
            try {
                Property property = form.toProperty();      // built by your PropertyFactory
                agent.addListing(property);
                catalog.add(property);
                redirect.addFlashAttribute("flashMessage", "Listing #" + property.getId() + " was added.");
                return "redirect:/manage";
            } catch (InvalidInputException | InvalidPriceException e) {
                errors.put("form", e.getMessage());         // your model's own validation as a safety net
            }
        }
        model.addAttribute("errors", errors);
        model.addAttribute("editing", false);
        return "manage-form";
    }

    // ---------------- edit ----------------
    @GetMapping("/manage/{id}/edit")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        Property property = load(id, currentUser.from(session));
        fillEditModel(model, property, PropertyForm.from(property), Map.of());
        return "manage-form";
    }

    @PostMapping("/manage/{id}")
    public String update(@PathVariable int id,
                         @ModelAttribute("form") PropertyForm form,
                         HttpSession session, Model model, RedirectAttributes redirect) {
        Property property = load(id, currentUser.from(session));
        Map<String, String> errors = form.validateForEdit(property);
        if (errors.isEmpty()) {
            try {
                form.applyTo(property);
                redirect.addFlashAttribute("flashMessage", "Listing #" + id + " was updated.");
                return "redirect:/manage";
            } catch (InvalidInputException | InvalidPriceException e) {
                errors.put("form", e.getMessage());
            }
        }
        fillEditModel(model, property, form, errors);
        return "manage-form";
    }

    // ---------------- status and delete ----------------
    @PostMapping("/manage/{id}/status")
    public String status(@PathVariable int id, @RequestParam String status,
                         HttpSession session, RedirectAttributes redirect) {
        Property property = load(id, currentUser.from(session));
        try {
            property.setStatus(ListingStatus.fromText(status));
            redirect.addFlashAttribute("flashMessage",
                    "#" + id + " " + property.getTitle() + " is now " + property.getStatus().getLabel() + ".");
        } catch (InvalidInputException e) {
            redirect.addFlashAttribute("flashError", e.getMessage());
        }
        return "redirect:/manage";
    }

    @PostMapping("/manage/{id}/delete")
    public String delete(@PathVariable int id, HttpSession session, RedirectAttributes redirect) {
        Property property = load(id, currentUser.from(session));
        try {
            catalog.remove(id);
        } catch (PropertyNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        for (Agent agent : users.getAllOfType(Agent.class)) {
            agent.removeListing(id);                 // keep the agents' lists tidy
        }
        for (Buyer buyer : users.getAllOfType(Buyer.class)) {
            buyer.removeFavourite(id);               // and nobody keeps a deleted favourite
        }
        redirect.addFlashAttribute("flashMessage", "Removed listing: " + property.getTitle());
        return "redirect:/manage";
    }

    // ---------------- helpers ----------------
    private Agent requireAgent(HttpSession session) {
        User user = currentUser.from(session);
        if (user instanceof Agent agent) {
            return agent;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only agents can add listings");
    }

    /** Finds the property and checks this user is allowed to change it. */
    private Property load(int id, User user) {
        try {
            Property property = catalog.findById(id);
            if (!user.canManage(property)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your listing");
            }
            return property;
        } catch (PropertyNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private void fillEditModel(Model model, Property property, PropertyForm form, Map<String, String> errors) {
        model.addAttribute("form", form);
        model.addAttribute("errors", errors);
        model.addAttribute("editing", true);
        model.addAttribute("p", PropertyCard.from(property));
        model.addAttribute("showBeds", property instanceof Residential);
        model.addAttribute("showParking", property instanceof Commercial);
    }
}
