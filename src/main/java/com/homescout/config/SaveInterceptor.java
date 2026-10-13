package com.homescout.config;

import com.homescout.service.PersistenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** After every successful change (a POST), the data is written to disk. */
@Component
public class SaveInterceptor implements HandlerInterceptor {
    private final PersistenceService persistence;

    public SaveInterceptor(PersistenceService persistence) {
        this.persistence = persistence;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if ("POST".equalsIgnoreCase(request.getMethod()) && ex == null) {
            persistence.saveAll();
        }
    }
}