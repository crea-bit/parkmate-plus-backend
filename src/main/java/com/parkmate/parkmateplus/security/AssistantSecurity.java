package com.parkmate.parkmateplus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.repository.AssistantRepository;
import com.parkmate.parkmateplus.repository.BookingRepository;

@Component("assistantSecurity")
public class AssistantSecurity {

    @Autowired
    private AssistantRepository assistantRepository;

    @Autowired
    private BookingRepository bookingRepository;


    // =========================================================
    // CHECK ASSISTANT OWNS ASSISTANT ID
    // =========================================================

    public boolean isOwner(Long assistantId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null) {

            return false;
        }

        Assistant assistant =
                assistantRepository.findByEmail(
                        authentication.getName()
                );

        if (assistant == null) {
            return false;
        }

        return assistant.getId()
                .equals(assistantId);
    }


    // =========================================================
    // CHECK ASSISTANT OWNS THE BOOKING
    // =========================================================
    // IMPORTANT:
    // Do NOT check booking status here.
    // The assistant must be allowed to move:
    //
    // ASSIGNED → PICKED_UP → PARKED → RETURNING → COMPLETED
    //
    // Only ownership is checked here.
    // =========================================================

    public boolean isAssignedToBooking(
            Long bookingId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null) {

            return false;
        }

        Assistant assistant =
                assistantRepository.findByEmail(
                        authentication.getName()
                );

        if (assistant == null) {
            return false;
        }

        Booking booking =
                bookingRepository.findById(
                        bookingId
                ).orElse(null);

        if (booking == null) {
            return false;
        }

        return booking.getAssistantId() != null
                && assistant.getId()
                        .equals(
                                booking.getAssistantId()
                        );
    }


    // =========================================================
    // CHECK ASSISTANT IS SELECTED FOR REQUESTED BOOKING
    // =========================================================

    public boolean isRequestedAssistant(
            Long bookingId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null) {

            return false;
        }

        Assistant assistant =
                assistantRepository.findByEmail(
                        authentication.getName()
                );

        if (assistant == null) {
            return false;
        }

        Booking booking =
                bookingRepository.findById(
                        bookingId
                ).orElse(null);

        if (booking == null) {
            return false;
        }

        return booking.getAssistantId() != null
                && assistant.getId()
                        .equals(
                                booking.getAssistantId()
                        )
                && "REQUESTED".equalsIgnoreCase(
                        booking.getStatus()
                );
    }
}