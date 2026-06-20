package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.Rating;
import com.parkmate.parkmateplus.entity.RatingAnalytics;
import com.parkmate.parkmateplus.service.RatingService;

@RestController
@RequestMapping("/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/add")
    public Rating addRating(@RequestBody Rating rating) {
        return ratingService.addRating(rating);
    }

    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/average")
    public double getAverageRating() {
        return ratingService.getAverageRating();
    }

    @GetMapping("/assistant-analytics")
    public List<RatingAnalytics> getAssistantRatingAnalytics() {
        return ratingService.getAssistantRatingAnalytics();
    }

    @GetMapping("/assistant/{assistantId}/average")
    public double getAverageRatingByAssistant(@PathVariable Long assistantId) {
        return ratingService.getAverageRatingByAssistant(assistantId);
    }
}