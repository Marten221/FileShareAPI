package com.example.FileShareAPI.Back_End.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200", "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://192.168.1.160:3000")); // Back end võib usaldada front endi ja töödelda andmeid, mis asub antud aadressil
                                                        //Saab mitu erinevat allikat lubada
        corsConfiguration.setAllowedHeaders(List.of(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, "X-Requested-With" ,ACCEPT, AUTHORIZATION, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS, "X-Registration-Code"));
        corsConfiguration.setExposedHeaders(List.of(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, "X-Requested-With",ACCEPT, AUTHORIZATION, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS, "X-Registration-Code"));
        corsConfiguration.setAllowedMethods(List.of(GET.name(), POST.name(), PUT.name(), DELETE.name(), OPTIONS.name()));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}
