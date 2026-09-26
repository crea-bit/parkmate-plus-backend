package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.dto.BookingDetailsDTO;
import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.service.BookingService;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;


    // =========================================================
    // HELPER - GET LOGGED-IN ASSISTANT
    // =========================================================

    private Assistant getLoggedInAssistant() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Authentication required"
            );
        }

        boolean isAssistant = false;

        for (GrantedAuthority authority :
                authentication.getAuthorities()) {

            if ("ROLE_ASSISTANT"
                    .equals(authority.getAuthority())) {

                isAssistant = true;
                break;
            }
        }

        if (!isAssistant) {

            throw new AccessDeniedException(
                    "Only assistants can access this API"
            );
        }

        Assistant assistant =
                bookingService.getAssistantByEmail(
                        authentication.getName()
                );

        if (assistant == null) {

            throw new AccessDeniedException(
                    "Assistant account not found"
            );
        }

        return assistant;
    }


    // =========================================================
    // HELPER - CHECK ASSISTANT OWNERSHIP
    // =========================================================

    private Assistant getOwnAssistant(Long assistantId) {

        Assistant assistant = getLoggedInAssistant();

        if (!assistant.getId().equals(assistantId)) {

            throw new AccessDeniedException(
                    "You can access only your own assistant data"
            );
        }

        return assistant;
    }


    // =========================================================
    // USER - CREATE BOOKING
    // =========================================================

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public Booking createBooking(
            @RequestBody Booking booking) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user =
                userRepository.findByEmail(
                        authentication.getName()
                );

        if (user == null) {
            throw new RuntimeException(
                    "User Not Found"
            );
        }

        // Never trust frontend userId
        booking.setUserId(user.getId());

        return bookingService.createBooking(
                booking
        );
    }


    // =========================================================
    // ADMIN - ASSIGN ASSISTANT
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookingId}/assign/{assistantId}")
    public Booking assignAssistant(
            @PathVariable Long bookingId,
            @PathVariable Long assistantId) {

        return bookingService.assignAssistant(
                bookingId,
                assistantId
        );
    }


    // =========================================================
    // ASSISTANT - ACCEPT BOOKING
    // =========================================================

    @PutMapping("/{bookingId}/accept")
    public Booking acceptBooking(
            @PathVariable Long bookingId) {

        Assistant assistant =
                getLoggedInAssistant();

        return bookingService.acceptBooking(
                bookingId,
                assistant.getId()
        );
    }


    // =========================================================
    // USER - VERIFY OTP
    // =========================================================

    @PreAuthorize(
            "hasRole('USER') and " +
            "@userSecurity.isBookingOwner(#bookingId)"
    )
    @PostMapping("/{bookingId}/verify/{otp}")
    public String verifyOtp(
            @PathVariable Long bookingId,
            @PathVariable String otp) {

        return bookingService.verifyOtp(
                bookingId,
                otp
        );
    }


    // =========================================================
    // ASSISTANT - UPDATE BOOKING STATUS
    // =========================================================

    @PutMapping("/{bookingId}/status/{status}")
    public Booking updateStatus(
            @PathVariable Long bookingId,
            @PathVariable String status) {

        Assistant assistant =
                getLoggedInAssistant();

        return bookingService.updateStatus(
                bookingId,
                status
        );
    }


    // =========================================================
    // USER - REQUEST RETURN
    // =========================================================

    @PreAuthorize(
            "hasRole('USER') and " +
            "@userSecurity.isBookingOwner(#bookingId)"
    )
    @PutMapping("/{bookingId}/request-return")
    public Booking requestReturn(
            @PathVariable Long bookingId) {

        return bookingService.requestReturn(
                bookingId
        );
    }


    // =========================================================
    // ASSISTANT - VIEW AVAILABLE REQUESTS
    // =========================================================

    @GetMapping("/available")
    public List<Booking> getAvailableRequests() {

        Assistant assistant =
                getLoggedInAssistant();

        return bookingService
                .getAvailableRequestsForAssistant(
                        assistant.getId()
                );
    }


    // =========================================================
    // ADMIN - VIEW ALL BOOKINGS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingService.getAllBookings();
    }


    // =========================================================
    // USER - VIEW OWN BOOKINGS
    // =========================================================

    @PreAuthorize(
            "hasRole('USER') and " +
            "@userSecurity.isOwner(#userId)"
    )
    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUser(
            @PathVariable Long userId) {

        return bookingService.getBookingsByUser(
                userId
        );
    }


    // =========================================================
    // ASSISTANT - VIEW OWN BOOKINGS
    // =========================================================

    @GetMapping("/assistant/{assistantId}")
    public List<Booking> getBookingsByAssistant(
            @PathVariable Long assistantId) {

        getOwnAssistant(assistantId);

        return bookingService.getBookingsByAssistant(
                assistantId
        );
    }


    // =========================================================
    // ASSISTANT - AVAILABLE REQUEST DETAILS
    // =========================================================

    @GetMapping("/details/available")
    public List<BookingDetailsDTO>
    getAvailableRequestDetails() {

        Assistant assistant =
                getLoggedInAssistant();

        return bookingService
                .getAvailableRequestDetailsForAssistant(
                        assistant.getId()
                );
    }


    // =========================================================
    // USER - OWN BOOKING DETAILS
    // =========================================================

    @PreAuthorize(
            "hasRole('USER') and " +
            "@userSecurity.isOwner(#userId)"
    )
    @GetMapping("/details/user/{userId}")
    public List<BookingDetailsDTO>
    getBookingDetailsByUser(
            @PathVariable Long userId) {

        return bookingService.getBookingDetailsByUser(
                userId
        );
    }


    // =========================================================
    // ASSISTANT - OWN BOOKING DETAILS
    // =========================================================

    @GetMapping("/details/assistant/{assistantId}")
    public List<BookingDetailsDTO>
    getBookingDetailsByAssistant(
            @PathVariable Long assistantId) {

        getOwnAssistant(assistantId);

        return bookingService
                .getBookingDetailsByAssistant(
                        assistantId
                );
    }


    // =========================================================
    // ADMIN - ALL BOOKING DETAILS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/details/all")
    public List<BookingDetailsDTO>
    getAllBookingDetails() {

        return bookingService.getAllBookingDetails();
    }
}