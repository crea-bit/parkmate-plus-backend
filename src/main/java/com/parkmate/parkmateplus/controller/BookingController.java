package com.parkmate.parkmateplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.dto.BookingDetailsDTO;
import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.service.BookingService;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @PutMapping("/{bookingId}/assign/{assistantId}")
    public Booking assignAssistant(
            @PathVariable Long bookingId,
            @PathVariable Long assistantId) {

        return bookingService.assignAssistant(bookingId, assistantId);
    }

    @PostMapping("/{bookingId}/verify/{otp}")
    public String verifyOtp(
            @PathVariable Long bookingId,
            @PathVariable String otp) {

        return bookingService.verifyOtp(bookingId, otp);
    }

    @PutMapping("/{bookingId}/status/{status}")
    public Booking updateStatus(
            @PathVariable Long bookingId,
            @PathVariable String status) {

        return bookingService.updateStatus(bookingId, status);
    }

    @PutMapping("/{bookingId}/request-return")
    public Booking requestReturn(@PathVariable Long bookingId) {
        return bookingService.requestReturn(bookingId);
    }

    @GetMapping("/available")
    public List<Booking> getAvailableRequests() {
        return bookingService.getAvailableRequests();
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUser(userId);
    }

    @GetMapping("/assistant/{assistantId}")
    public List<Booking> getBookingsByAssistant(@PathVariable Long assistantId) {
        return bookingService.getBookingsByAssistant(assistantId);
    }

    @GetMapping("/details/available")
    public List<BookingDetailsDTO> getAvailableRequestDetails() {
        return bookingService.getAvailableRequestDetails();
    }

    @GetMapping("/details/user/{userId}")
    public List<BookingDetailsDTO> getBookingDetailsByUser(@PathVariable Long userId) {
        return bookingService.getBookingDetailsByUser(userId);
    }

    @GetMapping("/details/assistant/{assistantId}")
    public List<BookingDetailsDTO> getBookingDetailsByAssistant(@PathVariable Long assistantId) {
        return bookingService.getBookingDetailsByAssistant(assistantId);
    }

    @GetMapping("/details/all")
    public List<BookingDetailsDTO> getAllBookingDetails() {
        return bookingService.getAllBookingDetails();
    }
}