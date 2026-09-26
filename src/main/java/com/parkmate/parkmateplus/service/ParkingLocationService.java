package com.parkmate.parkmateplus.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.ParkingLocation;
import com.parkmate.parkmateplus.repository.ParkingLocationRepository;

@Service
public class ParkingLocationService {

    @Autowired
    private ParkingLocationRepository parkingLocationRepository;


    // =========================================================
    // CREATE PARKING LOCATION
    // =========================================================

    public ParkingLocation createParkingLocation(
            ParkingLocation parkingLocation) {

        updateAvailability(parkingLocation);

        return parkingLocationRepository.save(
                parkingLocation
        );
    }


    // =========================================================
    // GET ALL PARKING LOCATIONS
    // =========================================================

    public List<ParkingLocation> getAllParkingLocations() {

        return parkingLocationRepository.findAll();
    }


    // =========================================================
    // GET AVAILABLE PARKING LOCATIONS
    // =========================================================

    public List<ParkingLocation> getAvailableParkingLocations() {

        return parkingLocationRepository
                .findByStatus("AVAILABLE");
    }


    // =========================================================
    // FIND PARKING WITHIN RADIUS
    // =========================================================

    public List<ParkingLocation> getNearbyParkingLocations(
            Double latitude,
            Double longitude,
            Double radiusKm) {

        List<ParkingLocation> allLocations =
                parkingLocationRepository.findAll();

        List<ParkingLocation> nearbyLocations =
                new ArrayList<>();

        for (ParkingLocation parking :
                allLocations) {

            if (parking.getLatitude() == null ||
                    parking.getLongitude() == null) {
                continue;
            }

            if (!"AVAILABLE".equals(
                    parking.getStatus())) {
                continue;
            }

            double distance =
                    calculateDistance(
                            latitude,
                            longitude,
                            parking.getLatitude(),
                            parking.getLongitude()
                    );

            if (distance <= radiusKm) {

                nearbyLocations.add(parking);
            }
        }

        return nearbyLocations;
    }


    // =========================================================
    // UPDATE PARKING
    // =========================================================

    public ParkingLocation updateParkingLocation(
            Long id,
            ParkingLocation updatedParking) {

        ParkingLocation parking =
                parkingLocationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Parking Location Not Found With ID : "
                                                + id
                                )
                        );

        parking.setName(
                updatedParking.getName()
        );

        parking.setLatitude(
                updatedParking.getLatitude()
        );

        parking.setLongitude(
                updatedParking.getLongitude()
        );

        parking.setTotalSpaces(
                updatedParking.getTotalSpaces()
        );

        parking.setOccupiedSpaces(
                updatedParking.getOccupiedSpaces()
        );

        updateAvailability(parking);

        return parkingLocationRepository.save(
                parking
        );
    }


    // =========================================================
    // DELETE PARKING
    // =========================================================

    public void deleteParkingLocation(Long id) {

        if (!parkingLocationRepository.existsById(id)) {

            throw new RuntimeException(
                    "Parking Location Not Found With ID : "
                            + id
            );
        }

        parkingLocationRepository.deleteById(id);
    }


    // =========================================================
    // UPDATE AVAILABILITY
    // =========================================================

    private void updateAvailability(
            ParkingLocation parking) {

        if (parking.getTotalSpaces() != null &&
                parking.getOccupiedSpaces() != null) {

            int available =
                    Math.max(
                            parking.getTotalSpaces()
                                    - parking.getOccupiedSpaces(),
                            0
                    );

            parking.setAvailableSpaces(
                    available
            );

            if (available == 0) {
                parking.setStatus("FULL");
            }
            else {
                parking.setStatus("AVAILABLE");
            }
        }
    }


    // =========================================================
    // HAVERSINE DISTANCE
    // =========================================================

    private double calculateDistance(
            Double lat1,
            Double lon1,
            Double lat2,
            Double lon2) {

        final int R = 6371;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                        + Math.cos(
                                Math.toRadians(lat1)
                        )
                        * Math.cos(
                                Math.toRadians(lat2)
                        )
                        * Math.sin(
                                lonDistance / 2
                        )
                        * Math.sin(
                                lonDistance / 2
                        );

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return R * c;
    }
}