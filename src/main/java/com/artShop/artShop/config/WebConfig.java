package com.artShop.artShop.config;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${hamachi.ip.address}")
    private String hamachiIpAddress;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Allowing CORS for origin: {}", hamachiIpAddress);
        registry.addMapping("/api/**")
                .allowedOrigins(hamachiIpAddress)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

