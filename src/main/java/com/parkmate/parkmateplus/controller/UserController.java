package com.parkmate.parkmateplus.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.service.JwtService;
import com.parkmate.parkmateplus.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    // =========================
    // REGISTER USER
    // =========================
    @PostMapping("/register")
    public User register(@RequestBody User user) {

        // Public registration can only create USER accounts
        user.setRole("USER");

        return userService.saveUser(user);
    }

    // =========================
    // LOGIN USER / ADMIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {

        User loggedInUser = userService.login(
                user.getEmail(),
                user.getPassword()
        );

        // Invalid email or password
        if (loggedInUser == null) {

            Map<String, String> errorResponse = new HashMap<>();

            errorResponse.put(
                    "message",
                    "Invalid email or password"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // Generate JWT token
        String token = jwtService.generateToken(loggedInUser);

        // Create login response
        Map<String, Object> response = new HashMap<>();

        response.put("token", token);
        response.put("user", loggedInUser);

        return ResponseEntity.ok(response);
    }
}