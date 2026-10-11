package com.homescout.config;

import com.homescout.model.Agent;
import com.homescout.service.PropertyCatalog;
import com.homescout.service.SearchService;
import com.homescout.util.SampleData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PropertyCatalog propertyCatalog() {
        PropertyCatalog catalog = PropertyCatalog.getInstance();   // your Singleton
        if (catalog.size() == 0) {
            Agent arjun = new Agent("Arjun Sharma", "arjun@agency.com", 0.02);
            Agent neha = new Agent("Neha Verma", "neha@agency.com", 0.015);
            SampleData.load(catalog, arjun, neha);
        }
        return catalog;
    }

    @Bean
    public SearchService searchService(PropertyCatalog catalog) {
        return new SearchService(catalog);
    }
}