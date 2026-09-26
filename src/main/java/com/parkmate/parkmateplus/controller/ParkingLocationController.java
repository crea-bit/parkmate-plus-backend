package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.ParkingLocation;
import com.parkmate.parkmateplus.service.ParkingLocationService;

@RestController
@RequestMapping("/parking")
@CrossOrigin(origins = "*")
public class ParkingLocationController {

    @Autowired
    private ParkingLocationService parkingLocationService;


    // =========================================================
    // ADMIN - CREATE PARKING LOCATION
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ParkingLocation createParkingLocation(
            @RequestBody ParkingLocation parkingLocation) {

        return parkingLocationService
                .createParkingLocation(
                        parkingLocation
                );
    }


    // =========================================================
    // ADMIN - GET ALL PARKING LOCATIONS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ParkingLocation>
    getAllParkingLocations() {

        return parkingLocationService
                .getAllParkingLocations();
    }


    // =========================================================
    // USER - GET AVAILABLE PARKING
    // =========================================================

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/available")
    public List<ParkingLocation>
    getAvailableParkingLocations() {

        return parkingLocationService
                .getAvailableParkingLocations();
    }


    // =========================================================
    // USER - NEARBY PARKING
    // =========================================================

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/nearby")
    public List<ParkingLocation>
    getNearbyParkingLocations(

            @RequestParam Double lat,

            @RequestParam Double lng,

            @RequestParam(
                    defaultValue = "2"
            )
            Double radius) {

        return parkingLocationService
                .getNearbyParkingLocations(
                        lat,
                        lng,
                        radius
                );
    }


    // =========================================================
    // ADMIN - UPDATE PARKING
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ParkingLocation
    updateParkingLocation(

            @PathVariable Long id,

            @RequestBody
            ParkingLocation parkingLocation) {

        return parkingLocationService
                .updateParkingLocation(
                        id,
                        parkingLocation
                );
    }


    // =========================================================
    // ADMIN - DELETE PARKING
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteParkingLocation(
            @PathVariable Long id) {

        parkingLocationService
                .deleteParkingLocation(id);

        return "Parking Location Deleted Successfully";
    }
}