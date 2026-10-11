package com.homescout.controller;

import com.homescout.filter.PropertyFilter;
import com.homescout.service.SearchService;
import com.homescout.sort.PriceAscendingSort;
import com.homescout.web.PropertyCard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ListingController {

    private final SearchService searchService;

    public ListingController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/listings")
    public String listings(Model model) {
        List<PropertyCard> cards = searchService
                .search(PropertyFilter.acceptAll(), new PriceAscendingSort())
                .stream()
                .map(PropertyCard::from)
                .collect(Collectors.toList());
        model.addAttribute("cards", cards);
        return "listings";
    }
}