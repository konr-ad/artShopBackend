package com.artShop.artShop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Adjust this based on your API routes
                .allowedOrigins("http://srv15.mikr.us:20158")  // Allow the frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Methods allowed
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true)  // Allow credentials (e.g., cookies)
                .maxAge(3600);  // Cache pre-flight requests for 1 hour
    }
}
