package com.parkmate.parkmateplus.security;

import java.util.List;

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

    // ==========================================
    // CHECK ASSIGNED ASSISTANT
    // ==========================================
    public boolean isAssignedToBooking(Long bookingId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        List<Assistant> assistants =
                assistantRepository.findAllByEmail(
                        authentication.getName());

        if (assistants.isEmpty()) {
            return false;
        }

        Booking booking =
                bookingRepository.findById(bookingId).orElse(null);

        if (booking == null || booking.getAssistantId() == null) {
            return false;
        }

        for (Assistant assistant : assistants) {

            if (booking.getAssistantId().equals(assistant.getId())) {
                return true;
            }
        }

        return false;
    }

    // ==========================================
    // CHECK REQUESTED ASSISTANT
    // ==========================================
    public boolean isRequestedAssistant(Long bookingId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        List<Assistant> assistants =
                assistantRepository.findAllByEmail(
                        authentication.getName());

        if (assistants.isEmpty()) {
            return false;
        }

        Booking booking =
                bookingRepository.findById(bookingId).orElse(null);

        if (booking == null
                || booking.getAssistantId() == null
                || !"REQUESTED".equals(booking.getStatus())) {
            return false;
        }

        for (Assistant assistant : assistants) {

            if (booking.getAssistantId().equals(assistant.getId())) {
                return true;
            }
        }

        return false;
    }

    // ==========================================
    // CHECK ASSISTANT OWNER
    // ==========================================
    public boolean isOwner(Long assistantId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        List<Assistant> assistants =
                assistantRepository.findAllByEmail(
                        authentication.getName());

        for (Assistant assistant : assistants) {

            if (assistant.getId().equals(assistantId)) {
                return true;
            }
        }

        return false;
    }
}