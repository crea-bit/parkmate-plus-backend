package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.Rating;
import com.parkmate.parkmateplus.entity.RatingAnalytics;
import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.service.RatingService;

@RestController
@RequestMapping("/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public Rating addRating(@RequestBody Rating rating) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user =
                userRepository.findByEmail(authentication.getName());

        if (user == null) {
            throw new RuntimeException("User Not Found");
        }

        // Never trust userId sent by the frontend.
        // Take it from the authenticated user.
        rating.setUserId(user.getId());

        return ratingService.addRating(rating);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/average")
    public double getAverageRating() {
        return ratingService.getAverageRating();
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/assistant-analytics")
    public List<RatingAnalytics> getAssistantRatingAnalytics() {
        return ratingService.getAssistantRatingAnalytics();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/assistant/{assistantId}/average")
    public double getAverageRatingByAssistant(
            @PathVariable Long assistantId) {

        return ratingService.getAverageRatingByAssistant(assistantId);
    }
}