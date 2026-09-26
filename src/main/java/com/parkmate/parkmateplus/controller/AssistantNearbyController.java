package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.dto.AssistantNearbyDTO;
import com.parkmate.parkmateplus.service.AssistantNearbyService;

@RestController
@RequestMapping("/assistants")
@CrossOrigin(origins = "*")
public class AssistantNearbyController {

    @Autowired
    private AssistantNearbyService assistantNearbyService;


    // =========================================================
    // GET NEARBY AVAILABLE ASSISTANTS
    // =========================================================

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/nearby")
    public List<AssistantNearbyDTO> getNearbyAssistants(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "2")
            Double radius) {

        // =====================================================
        // LATITUDE VALIDATION
        // =====================================================

        if (lat == null ||
                lat < -90 ||
                lat > 90) {

            throw new RuntimeException(
                    "Invalid latitude"
            );
        }


        // =====================================================
        // LONGITUDE VALIDATION
        // =====================================================

        if (lng == null ||
                lng < -180 ||
                lng > 180) {

            throw new RuntimeException(
                    "Invalid longitude"
            );
        }


        // =====================================================
        // RADIUS VALIDATION
        // =====================================================

        if (radius == null ||
                radius <= 0 ||
                radius > 10) {

            throw new RuntimeException(
                    "Radius must be between 0 and 10 km"
            );
        }


        return assistantNearbyService
                .getNearbyAssistants(
                        lat,
                        lng,
                        radius
                );
    }
}