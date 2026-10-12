package com.homescout.config;

import com.homescout.model.Admin;
import com.homescout.model.Agent;
import com.homescout.model.Buyer;
import com.homescout.model.User;
import com.homescout.service.AuthService;
import com.homescout.service.EnquiryService;
import com.homescout.service.PropertyCatalog;
import com.homescout.service.SearchService;
import com.homescout.service.UserService;
import com.homescout.util.SampleData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    public static final String DEMO_PASSWORD = "pass123";

    @Bean
    public UserService userService() {
        UserService users = new UserService();
        users.register(new Agent("Arjun Sharma", "arjun@agency.com", 0.02));
        users.register(new Agent("Neha Verma", "neha@agency.com", 0.015));
        users.register(new Buyer("Riya Singh", "riya@mail.com", 8_000_000));
        users.register(new Admin("Meera Rao", "admin@estate.com"));
        return users;
    }

    @Bean
    public AuthService authService(UserService users) {
        AuthService auth = new AuthService(users);
        for (User u : users.getAll()) {
            auth.setPassword(u.getEmail(), DEMO_PASSWORD);
        }
        return auth;
    }

    @Bean
    public EnquiryService enquiryService() {
        return new EnquiryService();
    }

    @Bean
    public PropertyCatalog propertyCatalog(UserService users) {
        PropertyCatalog catalog = PropertyCatalog.getInstance();      // your Singleton
        if (catalog.size() == 0) {
            List<Agent> agents = users.getAllOfType(Agent.class);     // your generic method
            SampleData.load(catalog, agents.get(0), agents.get(1));
        }
        return catalog;
    }

    @Bean
    public SearchService searchService(PropertyCatalog catalog) {
        return new SearchService(catalog);
    }
}