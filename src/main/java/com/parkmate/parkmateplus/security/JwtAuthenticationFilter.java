package com.parkmate.parkmateplus.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.parkmate.parkmateplus.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // No Authorization header
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token
        String token = authHeader.substring(7);

        try {

            // Check whether token is valid
            if (jwtService.isTokenValid(token)) {

                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);

                // Don't create authentication again
                if (email != null &&
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() == null) {

                    String authority = "ROLE_" + role;

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority(
                                                    authority
                                            )
                                    )
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Store authenticated user
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception e) {

            // Invalid JWT
            SecurityContextHolder.clearContext();
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}