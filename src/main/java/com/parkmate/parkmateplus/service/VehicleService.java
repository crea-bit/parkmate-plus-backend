package com.parkmate.parkmateplus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.entity.Vehicle;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.repository.VehicleRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    public Vehicle saveVehicle(Long userId, Vehicle vehicle) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User Not Found With ID : " + userId));

        vehicle.setUser(user);

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getVehiclesByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User Not Found With ID : " + userId));

        return vehicleRepository.findByUser(user);
    }
}