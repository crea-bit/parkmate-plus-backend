package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.parkmate.parkmateplus.entity.Notification;
import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasRole('USER') and @userSecurity.isOwner(#userId)")
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(
            @PathVariable Long userId) {

        return notificationService.getUserNotifications(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(
            @PathVariable Long notificationId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user =
                userRepository.findByEmail(authentication.getName());

        return notificationService.markAsRead(
                notificationId,
                user.getId()
        );
    }
}