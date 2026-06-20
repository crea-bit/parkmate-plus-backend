package com.parkmate.parkmateplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.Rating;
import com.parkmate.parkmateplus.entity.RatingAnalytics;
import com.parkmate.parkmateplus.repository.RatingRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public Rating addRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public double getAverageRating() {
        List<Rating> ratings = ratingRepository.findAll();

        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;

        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        return sum / ratings.size();
    }

    public double getAverageRatingByAssistant(Long assistantId) {

        List<Rating> ratings = ratingRepository.findByAssistantId(assistantId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;

        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        return sum / ratings.size();
    }

    public List<RatingAnalytics> getAssistantRatingAnalytics() {

        List<Rating> ratings = ratingRepository.findAll();

        Map<Long, List<Integer>> map = new HashMap<>();

        for (Rating rating : ratings) {
            Long assistantId = rating.getAssistantId();

            if (!map.containsKey(assistantId)) {
                map.put(assistantId, new ArrayList<>());
            }

            map.get(assistantId).add(rating.getRating());
        }

        List<RatingAnalytics> analyticsList = new ArrayList<>();

        for (Long assistantId : map.keySet()) {

            List<Integer> assistantRatings = map.get(assistantId);

            double sum = 0;

            for (Integer r : assistantRatings) {
                sum += r;
            }

            double avg = sum / assistantRatings.size();

            analyticsList.add(
                    new RatingAnalytics(
                            assistantId,
                            avg,
                            assistantRatings.size()
                    )
            );
        }

        return analyticsList;
    }
}