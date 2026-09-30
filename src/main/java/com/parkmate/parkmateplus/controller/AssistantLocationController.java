package com.parkmate.parkmateplus.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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


    // =========================================================
    // ASSISTANT - UPDATE OWN LOCATION
    // =========================================================

    @PreAuthorize(
        "hasRole('ASSISTANT') and " +
        "@assistantSecurity.isOwner(#assistantId)"
    )
    @PutMapping("/{assistantId}/location")
    public Assistant updateLocation(
            @PathVariable Long assistantId,
            @RequestBody AssistantLocationDTO location) {

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


        // =====================================================
        // VALIDATE LATITUDE
        // =====================================================

        if (location.getLatitude() < -90 ||
            location.getLatitude() > 90) {

            throw new RuntimeException(
                "Invalid latitude"
            );
        }


        // =====================================================
        // VALIDATE LONGITUDE
        // =====================================================

        if (location.getLongitude() < -180 ||
            location.getLongitude() > 180) {

            throw new RuntimeException(
                "Invalid longitude"
            );
        }


        // =====================================================
        // FIND ASSISTANT
        // =====================================================

        Assistant assistant =
            assistantRepository
                .findById(assistantId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Assistant account not found"
                    )
                );


        // =====================================================
        // UPDATE LOCATION
        // =====================================================

        assistant.setLatitude(
            location.getLatitude()
        );

        assistant.setLongitude(
            location.getLongitude()
        );

        assistant.setLastLocationUpdate(
            LocalDateTime.now()
        );


        // =====================================================
        // SAVE
        // =====================================================

        return assistantRepository.save(assistant);
    }
}