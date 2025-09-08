package com.artshop.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MediaResourceConfig implements WebMvcConfigurer {

    private final String location;

    public MediaResourceConfig(
            @Value("${app.media.storage-dir:./storage/media}") String storageDir
    ) {
        // file:///C:/.../storage/media/  (działa poprawnie na Windows/Linux)
        this.location = Paths.get(storageDir).toAbsolutePath().normalize().toUri().toString();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**")
                .addResourceLocations(location);
    }
}
