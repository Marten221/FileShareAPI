package com.example.FileShareAPI.Back_End.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "X-Registration-Code"));
        source.registerCorsConfiguration("/**", config);

        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // <-- ensures it runs before security
        return bean;
    }

    private List<String> getAllowedOrigins() {
        Dotenv dotenv = Dotenv.load();
        String allowedOrigins = dotenv.get("ALLOWED_CORS_ORIGINS");
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toList();
    }
}