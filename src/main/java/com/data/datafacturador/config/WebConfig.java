package com.data.datafacturador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web general.
 * El bean MultipartResolver explícito garantiza el soporte de subida de archivos
 * independientemente de las properties (soluciona "Failed to parse multipart servlet request").
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Habilita explícitamente el procesamiento de peticiones multipart (file upload).
     * Evita el error "Failed to parse multipart servlet request" en Spring Boot 4.x.
     */
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        resolver.setResolveLazily(true); // resuelve el multipart solo cuando se necesita
        return resolver;
    }
}
