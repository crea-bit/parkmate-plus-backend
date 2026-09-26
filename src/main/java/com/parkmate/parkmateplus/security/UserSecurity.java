package com.parkmate.parkmateplus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.entity.Notification;
import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.repository.BookingRepository;
import com.parkmate.parkmateplus.repository.NotificationRepository;
import com.parkmate.parkmateplus.repository.UserRepository;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BookingRepository bookingRepository;


    // =========================================================
    // Check whether the logged-in user owns the given user ID
    // =========================================================

    public boolean isOwner(Long userId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return false;
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return false;
        }

        return user.getId().equals(userId);
    }


    // =========================================================
    // Check whether the logged-in user owns the notification
    // =========================================================

    public boolean isNotificationOwner(Long notificationId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return false;
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return false;
        }

        Notification notification =
                notificationRepository.findById(notificationId).orElse(null);

        if (notification == null) {
            return false;
        }

        return notification.getUserId().equals(user.getId());
    }


    // =========================================================
    // Check whether the logged-in user owns the booking
    // =========================================================

    public boolean isBookingOwner(Long bookingId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return false;
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return false;
        }

        Booking booking =
                bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return false;
        }

        return user.getId().equals(booking.getUserId());
    }
}