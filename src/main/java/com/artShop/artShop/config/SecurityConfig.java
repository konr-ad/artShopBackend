package com.artShop.artShop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection using the new method
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll()  // Permit all requests
                )
                .headers(headers ->
                        headers.contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'; img-src 'self' http://localhost:4200;")
                        )
                );
        return http.build();
    }
}
