package com.parkmate.parkmateplus.repository;

import com.parkmate.parkmateplus.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    boolean existsByBookingIdAndStatus(
            Long bookingId,
            String status
    );
}