package com.homescout.controller;

import com.homescout.exception.InvalidInputException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.model.Agent;
import com.homescout.model.Buyer;
import com.homescout.model.Enquiry;
import com.homescout.model.Property;
import com.homescout.model.User;
import com.homescout.service.EnquiryService;
import com.homescout.service.PropertyCatalog;
import com.homescout.web.CurrentUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EnquiryController {

    private final EnquiryService enquiries;
    private final PropertyCatalog catalog;
    private final CurrentUser currentUser;

    public EnquiryController(EnquiryService enquiries, PropertyCatalog catalog, CurrentUser currentUser) {
        this.enquiries = enquiries;
        this.catalog = catalog;
        this.currentUser = currentUser;
    }

    /** Every role sees a different list. */
    @GetMapping("/enquiries")
    public String list(HttpSession session, Model model) {
        User user = currentUser.from(session);
        List<Enquiry> rows;
        String heading;
        if (user instanceof Buyer buyer) {
            rows = enquiries.forBuyer(buyer);
            heading = "My enquiries";
        } else if (user instanceof Agent agent) {
            rows = enquiries.forAgent(agent.getId());
            heading = "Enquiries about my listings";
        } else {
            rows = enquiries.all();
            heading = "All enquiries";
        }
        model.addAttribute("rows", rows);
        model.addAttribute("heading", heading);
        return "enquiries";
    }

    @PostMapping("/enquiries/{propertyId}")
    public String send(@PathVariable int propertyId,
                       @RequestParam String message,
                       HttpSession session,
                       RedirectAttributes redirect) {
        User user = currentUser.from(session);
        if (!(user instanceof Buyer buyer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only buyers can send enquiries");
        }
        try {
            Property property = catalog.findById(propertyId);
            enquiries.send(buyer, property, message);
            redirect.addFlashAttribute("flashMessage", "Your enquiry was sent to the agent.");
        } catch (PropertyNotFoundException | InvalidInputException e) {
            redirect.addFlashAttribute("flashError", e.getMessage());
        }
        return "redirect:/listings/" + propertyId;
    }
}
