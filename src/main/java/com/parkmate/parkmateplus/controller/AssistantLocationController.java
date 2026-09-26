package com.parkmate.parkmateplus.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkmate.parkmateplus.dto.AssistantLocationDTO;
import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.repository.AssistantRepository;

@RestController
@RequestMapping("/assistants")
@CrossOrigin(origins = "*")
public class AssistantLocationController {

    @Autowired
    private AssistantRepository assistantRepository;

    @PutMapping("/{assistantId}/location")
    public Assistant updateLocation(
            @PathVariable Long assistantId,
            @RequestBody AssistantLocationDTO location) {

        // =====================================================
        // AUTHENTICATION
        // =====================================================

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Authentication required"
            );
        }

        // =====================================================
        // CHECK ASSISTANT ROLE
        // =====================================================

        boolean isAssistant = false;

        for (GrantedAuthority authority :
                authentication.getAuthorities()) {

            if ("ROLE_ASSISTANT"
                    .equals(authority.getAuthority())) {

                isAssistant = true;
                break;
            }
        }

        if (!isAssistant) {

            throw new AccessDeniedException(
                    "Only assistants can update location"
            );
        }

        // =====================================================
        // FIND ASSISTANT BY ID
        // IMPORTANT:
        // We use findById because findByEmail() does not exist
        // in your AssistantRepository.
        // =====================================================

        Optional<Assistant> assistantOptional =
                assistantRepository.findById(assistantId);

        if (assistantOptional.isEmpty()) {

            throw new AccessDeniedException(
                    "Assistant account not found"
            );
        }

        Assistant loggedInAssistant =
                assistantOptional.get();

        // =====================================================
        // OWNERSHIP CHECK
        // =====================================================

        String loggedInEmail =
                authentication.getName();

        if (loggedInAssistant.getEmail() == null ||
                !loggedInAssistant.getEmail()
                        .equalsIgnoreCase(loggedInEmail)) {

            throw new AccessDeniedException(
                    "You can update only your own location"
            );
        }

        // =====================================================
        // VALIDATE LOCATION
        // =====================================================

        if (location == null ||
                location.getLatitude() == null ||
                location.getLongitude() == null) {

            throw new RuntimeException(
                    "Latitude and longitude are required"
            );
        }

        if (location.getLatitude() < -90 ||
                location.getLatitude() > 90) {

            throw new RuntimeException(
                    "Invalid latitude"
            );
        }

        if (location.getLongitude() < -180 ||
                location.getLongitude() > 180) {

            throw new RuntimeException(
                    "Invalid longitude"
            );
        }

        // =====================================================
        // UPDATE LOCATION
        // =====================================================

        loggedInAssistant.setLatitude(
                location.getLatitude()
        );

        loggedInAssistant.setLongitude(
                location.getLongitude()
        );

        loggedInAssistant.setLastLocationUpdate(
                LocalDateTime.now()
        );

        // =====================================================
        // SAVE
        // =====================================================

        return assistantRepository.save(
                loggedInAssistant
        );
    }
}