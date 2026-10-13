package com.homescout.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SaveInterceptor saveInterceptor;

    public WebConfig(SaveInterceptor saveInterceptor) {
        this.saveInterceptor = saveInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1) who may enter?
        registry.addInterceptor(new AccessInterceptor())
                .addPathPatterns("/favourites", "/favourites/**",
                        "/enquiries", "/enquiries/**",
                        "/manage", "/manage/**",
                        "/admin", "/admin/**");
        // 2) after every change, save to disk
        registry.addInterceptor(saveInterceptor)
                .addPathPatterns("/favourites/**", "/enquiries/**", "/manage/**");
    }
}