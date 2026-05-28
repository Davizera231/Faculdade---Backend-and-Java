package com.esteira.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${cors.allowed-origins:http://localhost:3000}") private String allowedOrigins;
    @Override public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/**").allowedOrigins(allowedOrigins).allowedMethods("GET","POST","PUT","DELETE","OPTIONS").allowedHeaders("*").allowCredentials(true);
    }
}
