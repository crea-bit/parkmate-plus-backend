package com.parkmate.parkmateplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.dto.BookingDetailsDTO;
import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.entity.Notification;
import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.entity.Vehicle;
import com.parkmate.parkmateplus.repository.AssistantRepository;
import com.parkmate.parkmateplus.repository.BookingRepository;
import com.parkmate.parkmateplus.repository.UserRepository;
import com.parkmate.parkmateplus.repository.VehicleRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AssistantRepository assistantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    public Booking createBooking(Booking booking) {

        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() ->
                        new RuntimeException("Vehicle Not Found"));

        // Vehicle must belong to logged-in user
        if (vehicle.getUser() == null
                || vehicle.getUser().getId() == null
                || !vehicle.getUser().getId().equals(booking.getUserId())) {

            throw new AccessDeniedException(
                    "You are not allowed to book this vehicle");
        }

        // Validate pickup and parking coordinates
        if (booking.getPickupLat() == null
                || booking.getPickupLng() == null
                || booking.getParkingLat() == null
                || booking.getParkingLng() == null) {

            throw new RuntimeException(
                    "Pickup and parking coordinates are required");
        }

        // Parking must be within 1 KM
        double distance = calculateDistance(
                booking.getPickupLat(),
                booking.getPickupLng(),
                booking.getParkingLat(),
                booking.getParkingLng()
        );

        if (distance > 1.0) {
            throw new RuntimeException(
                    "Parking location must be within 1 km of pickup location");
        }

        // If assistant selected, validate assistant
        if (booking.getAssistantId() != null) {

            Assistant assistant = assistantRepository
                    .findById(booking.getAssistantId())
                    .orElseThrow(() ->
                            new RuntimeException("Assistant Not Found"));

            if (assistant.getStatus() == null
                    || !"AVAILABLE".equalsIgnoreCase(assistant.getStatus())) {

                throw new RuntimeException(
                        "Selected assistant is not available");
            }
        }

        // Set initial booking status
        booking.setStatus("REQUESTED");

        // Generate 4-digit OTP
        String otp = String.format(
                "%04d",
                new Random().nextInt(10000)
        );

        booking.setOtp(otp);

        // Save booking
        Booking savedBooking = bookingRepository.save(booking);

        // Notification
        try {
            notificationService.createNotification(
                    booking.getUserId(),
                    "Booking #" + savedBooking.getId()
                            + " created successfully. Your OTP is "
                            + otp
            );
        } catch (Exception e) {
            System.out.println(
                    "Notification creation failed: "
                            + e.getMessage()
            );
        }

        return savedBooking;
    }

    // =========================================================
    // GET ASSISTANT BY EMAIL
    // =========================================================

    public Assistant getAssistantByEmail(String email) {

        List<Assistant> assistants =
                assistantRepository.findAllByEmail(email);

        if (assistants == null || assistants.isEmpty()) {
            return null;
        }

        return assistants.get(0);
    }

    // =========================================================
    // ASSIGN ASSISTANT - ADMIN
    // =========================================================

    public Booking assignAssistant(
            Long bookingId,
            Long assistantId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking Not Found With ID : "
                                        + bookingId));

        Assistant assistant = assistantRepository.findById(assistantId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assistant Not Found With ID : "
                                        + assistantId));

        if (assistant.getStatus() == null
                || !"AVAILABLE".equalsIgnoreCase(assistant.getStatus())) {

            throw new RuntimeException(
                    "Assistant is not available");
        }

        booking.setAssistantId(assistantId);
        booking.setStatus("ASSIGNED");

        Booking savedBooking = bookingRepository.save(booking);

        try {
            notificationService.createNotification(
                    booking.getUserId(),
                    "Assistant "
                            + assistant.getName()
                            + " has been assigned to your booking."
            );
        } catch (Exception e) {
            System.out.println(
                    "Notification creation failed: "
                            + e.getMessage()
            );
        }

        return savedBooking;
    }

    // =========================================================
    // ACCEPT BOOKING - ASSISTANT
    // =========================================================

    public Booking acceptBooking(
            Long bookingId,
            Long assistantId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking Not Found With ID : "
                                        + bookingId));

        // Make sure this booking is assigned to this assistant
        if (booking.getAssistantId() == null
                || !booking.getAssistantId().equals(assistantId)) {

            throw new AccessDeniedException(
                    "This booking is not assigned to you");
        }

        // Booking must be REQUESTED
        if (!"REQUESTED".equalsIgnoreCase(
                booking.getStatus())) {

            throw new RuntimeException(
                    "Booking is not in REQUESTED status");
        }

        booking.setStatus("ASSIGNED");

        Booking savedBooking = bookingRepository.save(booking);

        try {
            notificationService.createNotification(
                    booking.getUserId(),
                    "Your assistant has accepted booking #"
                            + booking.getId()
            );
        } catch (Exception e) {
            System.out.println(
                    "Notification creation failed: "
                            + e.getMessage()
            );
        }

        return savedBooking;
    }

    // =========================================================
    // VERIFY OTP
    // =========================================================

    public String verifyOtp(
            Long bookingId,
            String otp) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking Not Found With ID : "
                                        + bookingId));

        if (booking.getOtp() == null
                || !booking.getOtp().equals(otp)) {

            throw new RuntimeException("Invalid OTP");
        }

        booking.setStatus("PICKED_UP");

        bookingRepository.save(booking);

        return "OTP verified successfully";
    }

    // =========================================================
    // UPDATE BOOKING STATUS
    // =========================================================

    public Booking updateStatus(
            Long bookingId,
            String status) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking Not Found With ID : "
                                        + bookingId));

        booking.setStatus(status.toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);

        try {
            notificationService.createNotification(
                    booking.getUserId(),
                    "Booking #" + booking.getId()
                            + " status changed to "
                            + status.toUpperCase()
            );
        } catch (Exception e) {
            System.out.println(
                    "Notification creation failed: "
                            + e.getMessage()
            );
        }

        return savedBooking;
    }

    // =========================================================
    // REQUEST RETURN - USER
    // =========================================================

    public Booking requestReturn(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking Not Found With ID : "
                                        + bookingId));

        booking.setStatus("RETURN_REQUESTED");

        Booking savedBooking = bookingRepository.save(booking);

        try {
            if (booking.getAssistantId() != null) {
                notificationService.createNotification(
                        booking.getUserId(),
                        "Return request created for booking #"
                                + booking.getId()
                );
            }
        } catch (Exception e) {
            System.out.println(
                    "Notification creation failed: "
                            + e.getMessage()
            );
        }

        return savedBooking;
    }

    // =========================================================
    // AVAILABLE REQUESTS
    // =========================================================

    public List<Booking> getAvailableRequests() {

        return bookingRepository.findByStatus("REQUESTED");
    }

    // =========================================================
    // AVAILABLE REQUESTS FOR ASSISTANT
    // =========================================================

    public List<Booking> getAvailableRequestsForAssistant(
            Long assistantId) {

        List<Booking> all =
                bookingRepository.findByStatus("REQUESTED");

        List<Booking> result = new ArrayList<>();

        for (Booking booking : all) {

            if (booking.getAssistantId() != null
                    && booking.getAssistantId()
                    .equals(assistantId)) {

                result.add(booking);
            }
        }

        return result;
    }

    // =========================================================
    // ALL BOOKINGS
    // =========================================================

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }

    // =========================================================
    // USER BOOKINGS
    // =========================================================

    public List<Booking> getBookingsByUser(Long userId) {

        return bookingRepository.findByUserId(userId);
    }

    // =========================================================
    // ASSISTANT BOOKINGS
    // =========================================================

    public List<Booking> getBookingsByAssistant(
            Long assistantId) {

        return bookingRepository.findByAssistantId(assistantId);
    }

    // =========================================================
    // AVAILABLE REQUEST DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getAvailableRequestDetailsForAssistant(
            Long assistantId) {

        List<Booking> bookings =
                getAvailableRequestsForAssistant(assistantId);

        List<BookingDetailsDTO> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(convertToDTO(booking));
        }

        return result;
    }

    // =========================================================
    // USER BOOKING DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getBookingDetailsByUser(Long userId) {

        List<Booking> bookings =
                bookingRepository.findByUserId(userId);

        List<BookingDetailsDTO> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(convertToDTO(booking));
        }

        return result;
    }

    // =========================================================
    // ASSISTANT BOOKING DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getBookingDetailsByAssistant(
            Long assistantId) {

        List<Booking> bookings =
                bookingRepository.findByAssistantId(assistantId);

        List<BookingDetailsDTO> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(convertToDTO(booking));
        }

        return result;
    }

    // =========================================================
    // ALL BOOKING DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getAllBookingDetails() {

        List<Booking> bookings =
                bookingRepository.findAll();

        List<BookingDetailsDTO> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(convertToDTO(booking));
        }

        return result;
    }

    // =========================================================
    // CONVERT BOOKING TO DTO
    // =========================================================

    private BookingDetailsDTO convertToDTO(
            Booking booking) {

        BookingDetailsDTO dto = new BookingDetailsDTO();

        dto.setBookingId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setOtp(booking.getOtp());

        dto.setPickupLat(booking.getPickupLat());
        dto.setPickupLng(booking.getPickupLng());

        dto.setParkingLat(booking.getParkingLat());
        dto.setParkingLng(booking.getParkingLng());

        dto.setParkingLocation(
                booking.getParkingLocation());

        dto.setPickupLocation(
                booking.getPickupLocation());

        dto.setUserId(booking.getUserId());
        dto.setVehicleId(booking.getVehicleId());
        dto.setAssistantId(booking.getAssistantId());

        // User details
        if (booking.getUserId() != null) {

            User user =
                    userRepository.findById(
                            booking.getUserId())
                            .orElse(null);

            if (user != null) {
                dto.setUserName(user.getName());
                dto.setUserEmail(user.getEmail());
                dto.setUserPhone(user.getPhone());
            }
        }

        // Vehicle details
        if (booking.getVehicleId() != null) {

            Vehicle vehicle =
                    vehicleRepository.findById(
                            booking.getVehicleId())
                            .orElse(null);

            if (vehicle != null) {
                dto.setVehicleNumber(
                        vehicle.getVehicleNumber());
                dto.setVehicleType(
                        vehicle.getVehicleType());
            }
        }

        // Assistant details
        if (booking.getAssistantId() != null) {

            Assistant assistant =
                    assistantRepository.findById(
                            booking.getAssistantId())
                            .orElse(null);

            if (assistant != null) {
                dto.setAssistantName(
                        assistant.getName());
                dto.setAssistantEmail(
                        assistant.getEmail());
                dto.setAssistantPhone(
                        assistant.getPhone());
                dto.setAssistantStatus(
                        assistant.getStatus());
            }
        }

        return dto;
    }

    // =========================================================
    // HAVERSINE DISTANCE
    // =========================================================

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final int EARTH_RADIUS_KM = 6371;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}