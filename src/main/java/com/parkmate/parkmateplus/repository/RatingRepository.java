package com.parkmate.parkmateplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkmate.parkmateplus.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByAssistantId(Long assistantId);
    
}