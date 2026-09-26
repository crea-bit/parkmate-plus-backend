package com.parkmate.parkmateplus.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.dto.AssistantNearbyDTO;
import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.repository.AssistantRepository;

@Service
public class AssistantNearbyService {

    @Autowired
    private AssistantRepository assistantRepository;


    // =========================================================
    // FIND NEARBY AVAILABLE ASSISTANTS
    // =========================================================

    public List<AssistantNearbyDTO> getNearbyAssistants(
            Double latitude,
            Double longitude,
            Double radiusKm) {

        List<Assistant> assistants =
                assistantRepository.findAll();

        List<AssistantNearbyDTO> nearbyAssistants =
                new ArrayList<>();


        for (Assistant assistant : assistants) {

            // -------------------------------------------------
            // ONLY AVAILABLE
            // -------------------------------------------------

            if (!"AVAILABLE".equalsIgnoreCase(
                    assistant.getStatus())) {

                continue;
            }


            // -------------------------------------------------
            // LOCATION REQUIRED
            // -------------------------------------------------

            if (assistant.getLatitude() == null ||
                    assistant.getLongitude() == null) {

                continue;
            }


            // -------------------------------------------------
            // DISTANCE
            // -------------------------------------------------

            double distance =
                    calculateDistance(
                            latitude,
                            longitude,
                            assistant.getLatitude(),
                            assistant.getLongitude()
                    );


            // -------------------------------------------------
            // WITHIN RADIUS
            // -------------------------------------------------

            if (distance <= radiusKm) {

                double roundedDistance =
                        Math.round(
                                distance * 100.0
                        ) / 100.0;

                AssistantNearbyDTO dto =
                        new AssistantNearbyDTO(

                                assistant.getId(),

                                assistant.getName(),

                                assistant.getPhone(),

                                assistant.getLatitude(),

                                assistant.getLongitude(),

                                assistant.getStatus(),

                                assistant.getRating(),

                                roundedDistance
                        );

                nearbyAssistants.add(dto);
            }
        }


        // =====================================================
        // NEAREST FIRST
        // =====================================================

        nearbyAssistants.sort(
                Comparator.comparing(
                        AssistantNearbyDTO
                                ::getDistanceKm
                )
        );


        return nearbyAssistants;
    }


    // =========================================================
    // HAVERSINE DISTANCE
    // =========================================================

    private double calculateDistance(
            Double lat1,
            Double lon1,
            Double lat2,
            Double lon2) {

        final double EARTH_RADIUS_KM =
                6371.0;

        double latDistance =
                Math.toRadians(
                        lat2 - lat1
                );

        double lonDistance =
                Math.toRadians(
                        lon2 - lon1
                );

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)

                        +

                        Math.cos(
                                Math.toRadians(lat1)
                        )

                        * Math.cos(
                                Math.toRadians(lat2)
                        )

                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS_KM * c;
    }
}