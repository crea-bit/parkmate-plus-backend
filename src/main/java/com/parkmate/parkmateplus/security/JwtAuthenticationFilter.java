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
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");


        // =====================================================
        // NO TOKEN
        // =====================================================

        if (authHeader == null ||
            !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        String token =
                authHeader.substring(7);


        try {

            // =================================================
            // VALIDATE TOKEN
            // =================================================

            if (jwtService.isTokenValid(token)) {

                String email =
                        jwtService.extractEmail(token);

                String role =
                        jwtService.extractRole(token);


                // =================================================
                // CREATE AUTHENTICATION
                // =================================================

                if (email != null &&
                    role != null &&
                    SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

                    String authority =
                            "ROLE_" + role;


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


                    SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                            authentication
                        );
                }
            }

        } catch (Exception e) {

            SecurityContextHolder
                .clearContext();

            System.out.println(
                "JWT authentication failed: "
                + e.getMessage()
            );
        }


        filterChain.doFilter(
            request,
            response
        );
    }
}