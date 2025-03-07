package com.planify.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("🚀 CORS configuration is being loaded!");
        registry.addMapping("/**") // Toutes les routes de l'application
                .allowedOrigins("http://localhost:5173") // Origines autorisées
                .allowedMethods("*") // Méthodes HTTP autorisées
                .allowedHeaders("*") // Tous les headers autorisés
                .allowCredentials(true); // Autoriser les cookies ou identifiants
    }
}
