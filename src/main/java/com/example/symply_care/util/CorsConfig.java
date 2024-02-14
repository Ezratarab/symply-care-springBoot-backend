package com.example.symply_care.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/doctors/**")  // this maps all the CRUD operations for /employees endpoint
                .allowedOrigins(ALLOWED_ORIGIN)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // specify the HTTP methods you want to allow
                .allowCredentials(true)
                .maxAge(3600);
        registry.addMapping("/patients/**")  // this maps all the CRUD operations for /employees endpoint
                .allowedOrigins(ALLOWED_ORIGIN)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // specify the HTTP methods you want to allow
                .allowCredentials(true)
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGIN)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // specify the HTTP methods you want to allow
                .allowCredentials(true)
                .maxAge(3600);
    }
}
