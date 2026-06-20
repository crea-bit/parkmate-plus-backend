package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.Vehicle;
import com.parkmate.parkmateplus.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/add/{userId}")
    public Vehicle addVehicle(
            @PathVariable Long userId,
            @RequestBody Vehicle vehicle) {

        return vehicleService.saveVehicle(userId, vehicle);
    }

    @GetMapping("/user/{userId}")
    public List<Vehicle> getVehiclesByUser(@PathVariable Long userId) {
        return vehicleService.getVehiclesByUser(userId);
    }
}