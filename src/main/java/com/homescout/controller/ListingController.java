package com.homescout.controller;

import com.homescout.exception.InvalidInputException;
import com.homescout.exception.InvalidPriceException;
import com.homescout.filter.FilterCriteria;
import com.homescout.filter.PropertyFilter;
import com.homescout.filter.StatusFilter;
import com.homescout.model.Buyer;
import com.homescout.model.ListingStatus;
import com.homescout.model.Property;
import com.homescout.model.PropertyType;
import com.homescout.model.User;
import com.homescout.service.SearchService;
import com.homescout.web.CurrentUser;
import com.homescout.web.FilterForm;
import com.homescout.web.PropertyCard;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ListingController {

    private static final int PAGE_SIZE = 6;

    private final SearchService searchService;
    private final CurrentUser currentUser;

    public ListingController(SearchService searchService, CurrentUser currentUser) {
        this.searchService = searchService;
        this.currentUser = currentUser;
    }

    // These three lists fill the checkboxes and the city dropdown on the page.
    @ModelAttribute("typeOptions")
    public List<String> typeOptions() {
        return Arrays.stream(PropertyType.values())
                .map(PropertyType::getLabel)
                .collect(Collectors.toList());
    }

    @ModelAttribute("amenityOptions")
    public List<String> amenityOptions() {
        return List.of("Lift", "Pool", "Garden", "Parking", "Gym");
    }

    @ModelAttribute("cityOptions")
    public List<String> cityOptions() {
        return searchService.search(PropertyFilter.acceptAll()).stream()
                .map(Property::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @GetMapping("/listings")
    public String listings(@ModelAttribute("form") FilterForm form, HttpSession session, Model model) {
        List<Property> pageItems = List.of();
        String description = "";
        int matches = 0;
        int totalPages = 1;
        int currentPage = 1;

        try {
            FilterCriteria.Builder builder = form.toCriteria();
            PropertyFilter filter = builder.build();               // a CompositeFilter
            description = builder.describe();

            if (form.isOnlyAvailable()) {
                filter = filter.and(new StatusFilter(ListingStatus.AVAILABLE));   // default method and()
                description += " | only available listings";
            }

            List<Property> results = searchService.search(filter, form.sortStrategy());
            matches = results.size();
            totalPages = Math.max(1, (matches + PAGE_SIZE - 1) / PAGE_SIZE);
            currentPage = Math.min(Math.max(form.getPage(), 1), totalPages);
            pageItems = searchService.page(results, currentPage, PAGE_SIZE);

        } catch (InvalidPriceException e) {
            model.addAttribute("error",
                    "Check the price range: the minimum must not be above the maximum, and neither can be negative.");
        } catch (InvalidInputException e) {
            model.addAttribute("error", e.getMessage());
        }

        // which properties has the logged-in buyer saved? (used to colour the Save buttons)
        Set<Integer> favouriteIds = new HashSet<>();
        User user = currentUser.from(session);
        if (user instanceof Buyer buyer) {
            buyer.getFavourites().forEach(p -> favouriteIds.add(p.getId()));
        }

        String query = form.toQueryString();
        model.addAttribute("cards", pageItems.stream().map(PropertyCard::from).collect(Collectors.toList()));
        model.addAttribute("description", description);
        model.addAttribute("matches", matches);
        model.addAttribute("total", searchService.search(PropertyFilter.acceptAll()).size());
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("query", query);
        model.addAttribute("favouriteIds", favouriteIds);
        model.addAttribute("next", "/listings?" + query + "page=" + currentPage);
        return "listings";
    }
}