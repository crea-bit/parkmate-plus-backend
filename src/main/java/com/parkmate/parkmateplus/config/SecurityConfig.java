package com.parkmate.parkmateplus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.parkmate.parkmateplus.security.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

            // REST API - disable CSRF
            .csrf(csrf -> csrf.disable())

            // Enable CORS
            .cors(cors -> {})

            // JWT is stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // =========================
                // PUBLIC ENDPOINTS
                // =========================

                .requestMatchers(
                    "/users/register",
                    "/users/login"
                ).permitAll()

                .requestMatchers(
                    "/assistants/register",
                    "/assistants/login"
                ).permitAll()

                // H2 console - development only
                .requestMatchers(
                    "/h2-console/**"
                ).permitAll()

                // Browser CORS preflight
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                // =========================
                // ADMIN ENDPOINTS
                // =========================

                .requestMatchers(
                    "/admin/**"
                ).hasRole("ADMIN")

                // =========================
                // ALL OTHER PROTECTED APIs
                // =========================

                .anyRequest().authenticated()
            )

            // H2 console support
            .headers(headers ->
                headers.frameOptions(frame ->
                    frame.sameOrigin()
                )
            )

            // JWT filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}