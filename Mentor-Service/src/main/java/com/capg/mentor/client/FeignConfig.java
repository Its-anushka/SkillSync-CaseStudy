package com.capg.mentor.client;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            var context = SecurityContextHolder.getContext();

            if (context.getAuthentication() != null) {

                Object credentials = context.getAuthentication().getCredentials();

                if (credentials != null) {
                    requestTemplate.header("Authorization", "Bearer " + credentials.toString());
                }
            }
        };
    }
}