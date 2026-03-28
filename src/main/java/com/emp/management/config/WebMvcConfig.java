package com.emp.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getProperty("user.dir") + "/uploads/profile-photos/";
        
        // Create folder if not exists
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (Exception ignored) {}

        registry.addResourceHandler("/uploads/profile-photos/**")
                .addResourceLocations("file:" + uploadPath);
    }
}