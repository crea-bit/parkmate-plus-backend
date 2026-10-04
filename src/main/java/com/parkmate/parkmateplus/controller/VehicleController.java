package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.entity.Vehicle;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.service.CloudinaryService;
import com.parkmate.parkmateplus.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    // =========================================================
    // ADD VEHICLE
    // =========================================================

    @PreAuthorize("hasRole('USER')")
    @PostMapping(
        value = "/add",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Vehicle addVehicle(
            Authentication authentication,
            @RequestPart("vehicle") Vehicle vehicle,
            @RequestPart(
                value = "image",
                required = false
            ) MultipartFile image) {

        User user = userRepository.findByEmail(
                authentication.getName()
        );

        if (user == null) {
            throw new RuntimeException("User Not Found");
        }

        // ---------------------------------------------------------
        // Upload vehicle image to Cloudinary
        // ---------------------------------------------------------

        if (image != null && !image.isEmpty()) {

            String imageUrl =
                    cloudinaryService.uploadImage(image);

            vehicle.setImageUrl(imageUrl);
        }

        // ---------------------------------------------------------
        // Save vehicle for logged-in user
        // ---------------------------------------------------------

        return vehicleService.saveVehicle(
                user.getId(),
                vehicle
        );
    }


    // =========================================================
    // GET USER VEHICLES
    // =========================================================

    @PreAuthorize(
        "hasRole('USER') and @userSecurity.isOwner(#userId)"
    )
    @GetMapping("/user/{userId}")
    public List<Vehicle> getVehiclesByUser(
            @PathVariable Long userId) {

        return vehicleService.getVehiclesByUser(userId);
    }
}