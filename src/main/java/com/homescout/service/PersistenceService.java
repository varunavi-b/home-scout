package com.homescout.service;

import com.homescout.exception.DataFileException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/** Saves everything to disk, and brings favourites and enquiries back when the website starts. */
@Component
public class PersistenceService {
    private final DataStore store;
    private final PropertyCatalog catalog;
    private final UserService users;
    private final EnquiryService enquiries;

    public PersistenceService(DataStore store, PropertyCatalog catalog, UserService users, EnquiryService enquiries) {
        this.store = store;
        this.catalog = catalog;
        this.users = users;
        this.enquiries = enquiries;
    }

    @PostConstruct                                  // runs once, right after the website starts
    public void restore() {
        try {
            store.loadExtras(users, enquiries, catalog);
        } catch (DataFileException e) {
            System.out.println("Warning: could not restore favourites and enquiries - " + e.getMessage());
        }
    }

    public synchronized boolean saveAll() {
        try {
            store.saveProperties(catalog.getAll());
            store.saveExtras(users, enquiries);
            return true;
        } catch (DataFileException e) {
            System.out.println("Warning: could not save data - " + e.getMessage());
            return false;
        }
    }

    @PreDestroy                                     // runs once, when the website shuts down
    public void saveOnShutdown() {
        saveAll();
    }
}