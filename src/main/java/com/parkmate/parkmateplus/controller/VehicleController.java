package com.parkmate.parkmateplus.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.parkmate.parkmateplus.entity.Vehicle;
import com.parkmate.parkmateplus.service.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    
    @PreAuthorize("hasRole('USER') and @userSecurity.isOwner(#userId)")
    @PostMapping("/add/{userId}")
    public Vehicle addVehicle(
            @PathVariable Long userId,
            @RequestBody Vehicle vehicle) {

        return vehicleService.saveVehicle(userId, vehicle);
    }
    
    @PreAuthorize("hasRole('USER') and @userSecurity.isOwner(#userId)")
    @GetMapping("/user/{userId}")
    public List<Vehicle> getVehiclesByUser(@PathVariable Long userId) {
        return vehicleService.getVehiclesByUser(userId);
    }
}