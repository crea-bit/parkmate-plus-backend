package com.parkmate.parkmateplus.service;

import java.nio.charset.StandardCharsets;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.parkmate.parkmateplus.entity.Assistant;
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Create secret signing key
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Generate JWT token
    public String generateToken(User user) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
 // Generate JWT token for Assistant
    public String generateToken(Assistant assistant) {

        Date now = new Date();

        Date expiryDate =
                new Date(
                    now.getTime() + expiration
                );

        return Jwts.builder()
                .subject(assistant.getEmail())
                .claim("userId", assistant.getId())
                .claim("role", "ASSISTANT")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }    // Extract email from token
    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    // Extract user ID from token
    public Long extractUserId(String token) {

        return extractAllClaims(token)
                .get("userId", Long.class);
    }

    // Check whether token is valid
    public boolean isTokenValid(String token) {

        try {

            extractAllClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}