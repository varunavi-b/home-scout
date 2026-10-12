package com.homescout.controller;

import com.homescout.exception.PropertyNotFoundException;
import com.homescout.model.Buyer;
import com.homescout.model.Property;
import com.homescout.service.PropertyCatalog;
import com.homescout.web.CurrentUser;
import com.homescout.web.PropertyCard;
import com.homescout.web.Redirects;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
public class FavouriteController {

    private final PropertyCatalog catalog;
    private final CurrentUser currentUser;

    public FavouriteController(PropertyCatalog catalog, CurrentUser currentUser) {
        this.catalog = catalog;
        this.currentUser = currentUser;
    }

    @GetMapping("/favourites")
    public String list(HttpSession session, Model model) {
        Buyer buyer = (Buyer) currentUser.from(session);        // the access guard already checked: buyers only
        model.addAttribute("cards", buyer.getFavourites().stream()
                .map(PropertyCard::from)
                .collect(Collectors.toList()));
        return "favourites";
    }

    @PostMapping("/favourites/{id}/toggle")
    public String toggle(@PathVariable int id,
                         @RequestParam(required = false) String next,
                         HttpSession session,
                         RedirectAttributes redirect) {
        Buyer buyer = (Buyer) currentUser.from(session);
        try {
            Property property = catalog.findById(id);
            if (buyer.removeFavourite(id)) {
                redirect.addFlashAttribute("flashMessage", "Removed from favourites: " + property.getTitle());
            } else {
                buyer.addFavourite(property);
                redirect.addFlashAttribute("flashMessage", "Saved to favourites: " + property.getTitle());
            }
        } catch (PropertyNotFoundException e) {
            redirect.addFlashAttribute("flashError", e.getMessage());
        }
        return "redirect:" + Redirects.safe(next, "/favourites");
    }
}
