package com.homescout.controller;

import com.homescout.service.EnquiryService;
import com.homescout.service.PropertyCatalog;
import com.homescout.service.SearchService;
import com.homescout.service.UserService;
import com.homescout.web.ConceptsReport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** A public page that shows every OOP concept working on the live data. Handy for the viva. */
@Controller
public class ConceptsController {

    private final PropertyCatalog catalog;
    private final SearchService search;
    private final UserService users;
    private final EnquiryService enquiries;

    public ConceptsController(PropertyCatalog catalog, SearchService search, UserService users, EnquiryService enquiries) {
        this.catalog = catalog;
        this.search = search;
        this.users = users;
        this.enquiries = enquiries;
    }

    @GetMapping("/concepts")
    public String concepts(Model model) {
        model.addAttribute("sections", ConceptsReport.build(catalog, search, users, enquiries));
        return "concepts";
    }
}