package com.parkmate.parkmateplus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.parkmate.parkmateplus.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

            // =====================================================
            // CSRF
            // =====================================================
            .csrf(csrf -> csrf.disable())

            // =====================================================
            // CORS
            // =====================================================
            .cors(cors -> {})

            // =====================================================
            // SESSION
            // =====================================================
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // =====================================================
            // AUTHORIZATION
            // =====================================================
            .authorizeHttpRequests(auth -> auth

                // -------------------------------------------------
                // PUBLIC USER APIs
                // -------------------------------------------------
            		.requestMatchers(
            			    "/users/register",
            			    "/users/login"
            			).permitAll()

                // -------------------------------------------------
                // PUBLIC ASSISTANT APIs
                // -------------------------------------------------
                .requestMatchers(
                    "/assistants/register",
                    "/assistants/login"
                ).permitAll()

                // -------------------------------------------------
                // H2 CONSOLE
                // -------------------------------------------------
                .requestMatchers(
                    "/h2-console/**"
                ).permitAll()

                // -------------------------------------------------
                // CORS PREFLIGHT
                // -------------------------------------------------
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                // -------------------------------------------------
                // ADMIN APIs
                // -------------------------------------------------
                .requestMatchers(
                    "/admin/**"
                ).hasRole("ADMIN")

                // -------------------------------------------------
                // ASSISTANT LOCATION
                //
                // AssistantLocationController performs:
                // 1. Authentication check
                // 2. ASSISTANT role check
                // 3. Assistant ownership check
                // -------------------------------------------------
                .requestMatchers(
                    HttpMethod.PUT,
                    "/assistants/*/location"
                ).permitAll()

                // -------------------------------------------------
                // OTHER ASSISTANT APIs
                // -------------------------------------------------
                .requestMatchers(
                    "/assistants/**"
                ).hasRole("ASSISTANT")

                // -------------------------------------------------
                // EVERYTHING ELSE
                // -------------------------------------------------
                .anyRequest().authenticated()
            )

            // =====================================================
            // H2 FRAME SUPPORT
            // =====================================================
            .headers(headers ->
                headers.frameOptions(frame ->
                    frame.sameOrigin()
                )
            )

            // =====================================================
            // JWT FILTER
            // =====================================================
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}