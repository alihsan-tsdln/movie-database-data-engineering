package org.bigDataFactory.api;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3002", "http://localhost:3003")  // İzin verilen origin (kaynak) adres
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // İzin verilen HTTP metodlar
                .allowedHeaders("*")  // İzin verilen başlıklar
                .allowCredentials(true);  // Cookie'lerin veya kimlik doğrulama bilgilerin gönderilmesine izin verir
    }
}
