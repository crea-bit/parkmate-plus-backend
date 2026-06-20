package com.parkmate.parkmateplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByUser(User user);
}