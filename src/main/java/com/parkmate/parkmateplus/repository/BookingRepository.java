package com.parkmate.parkmateplus.repository;

import com.parkmate.parkmateplus.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByAssistantId(Long assistantId);
    
    List<Booking> findByStatus(String status);
}