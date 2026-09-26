package com.parkmate.parkmateplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkmate.parkmateplus.entity.ParkingLocation;

public interface ParkingLocationRepository
        extends JpaRepository<ParkingLocation, Long> {

    List<ParkingLocation> findByStatus(String status);
}