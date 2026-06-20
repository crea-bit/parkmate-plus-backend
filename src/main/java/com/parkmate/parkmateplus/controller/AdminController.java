package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.entity.Vehicle;
import com.parkmate.parkmateplus.repository.AssistantRepository;
import com.parkmate.parkmateplus.repository.BookingRepository;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.repository.VehicleRepository;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssistantRepository assistantRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/users/count")
    public long getUserCount() {
        return userRepository.count();
    }

    @GetMapping("/assistants/count")
    public long getAssistantCount() {
        return assistantRepository.count();
    }

    @GetMapping("/vehicles/count")
    public long getVehicleCount() {
        return vehicleRepository.count();
    }

    @GetMapping("/bookings/count")
    public long getBookingCount() {
        return bookingRepository.count();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/assistants")
    public List<Assistant> getAllAssistants() {
        return assistantRepository.findAll();
    }

    @GetMapping("/vehicles")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}