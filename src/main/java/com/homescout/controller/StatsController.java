package com.homescout.controller;

import com.homescout.exception.DataFileException;
import com.homescout.service.EnquiryService;
import com.homescout.service.PropertyCatalog;
import com.homescout.service.SearchService;
import com.homescout.service.UserService;
import com.homescout.util.FileHandler;
import com.homescout.web.StatsView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Admin-only pages (the access guard protects everything under /admin). */
@Controller
public class StatsController {

    private final SearchService search;
    private final PropertyCatalog catalog;
    private final UserService users;
    private final EnquiryService enquiries;

    public StatsController(SearchService search, PropertyCatalog catalog, UserService users, EnquiryService enquiries) {
        this.search = search;
        this.catalog = catalog;
        this.users = users;
        this.enquiries = enquiries;
    }

    @GetMapping("/admin/stats")
    public String stats(Model model) {
        model.addAttribute("s", StatsView.build(search, catalog, users, enquiries));
        return "stats";
    }

    /** Downloads every listing as a CSV file, using your FileHandler.exportCsv(). */
    @GetMapping("/admin/export")
    public ResponseEntity<byte[]> export() {
        try {
            Path temp = Files.createTempFile("listings", ".csv");
            try {
                FileHandler.exportCsv(temp, catalog.getAll());
                byte[] bytes = Files.readAllBytes(temp);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=listings.csv")
                        .contentType(MediaType.parseMediaType("text/csv"))
                        .body(bytes);
            } finally {
                Files.deleteIfExists(temp);
            }
        } catch (DataFileException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create the CSV file");
        }
    }
}