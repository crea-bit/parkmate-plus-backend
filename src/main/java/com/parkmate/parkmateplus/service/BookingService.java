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
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AssistantRepository assistantRepository;


    // =========================================================
    // CREATE BOOKING
    // =========================================================

    public Booking createBooking(Booking booking) {

        // =====================================================
        // VEHICLE OWNERSHIP CHECK
        // =====================================================

        if (booking.getVehicleId() != null) {

            Vehicle vehicle = vehicleRepository
                    .findById(booking.getVehicleId())
                    .orElseThrow(() -> new RuntimeException(
                            "Vehicle Not Found With ID : "
                                    + booking.getVehicleId()
                    ));

            if (vehicle.getUser() == null ||
                    !vehicle.getUser()
                            .getId()
                            .equals(booking.getUserId())) {

                throw new AccessDeniedException(
                        "You are not authorized to use this vehicle"
                );
            }
        }


        // =====================================================
        // PARKING LOCATION MUST BE WITHIN 1 KM
        // =====================================================

        if (booking.getPickupLat() != null &&
                booking.getPickupLng() != null &&
                booking.getParkingLat() != null &&
                booking.getParkingLng() != null) {

            double distance = calculateDistance(
                    booking.getPickupLat(),
                    booking.getPickupLng(),
                    booking.getParkingLat(),
                    booking.getParkingLng()
            );

            if (distance > 1.0) {

                throw new RuntimeException(
                        "Parking location must be within 1 km"
                );
            }
        }


        // =====================================================
        // SELECTED ASSISTANT CHECK
        // =====================================================

        if (booking.getAssistantId() != null) {

            Assistant assistant = assistantRepository
                    .findById(booking.getAssistantId())
                    .orElseThrow(() -> new RuntimeException(
                            "Assistant Not Found With ID : "
                                    + booking.getAssistantId()
                    ));

            if (!"AVAILABLE".equalsIgnoreCase(
                    assistant.getStatus())) {

                throw new RuntimeException(
                        "Selected assistant is not available"
                );
            }
        }


        // =====================================================
        // BOOKING STATUS
        // =====================================================

        booking.setStatus("REQUESTED");

        // IMPORTANT:
        // Do NOT set assistantId to null.
        // Selected assistant remains attached to booking.


        // =====================================================
        // GENERATE OTP
        // =====================================================

        String otp = String.valueOf(
                1000 + new Random().nextInt(9000)
        );

        booking.setOtp(otp);


        // =====================================================
        // SAVE BOOKING
        // =====================================================

        Booking savedBooking =
                bookingRepository.save(booking);


        // =====================================================
        // USER NOTIFICATION
        // =====================================================

        notificationService.createNotification(
                savedBooking.getUserId(),
                "Your parking request has been created. Booking ID: "
                        + savedBooking.getId()
        );

        return savedBooking;
    }


    // =========================================================
    // CALCULATE DISTANCE
    // =========================================================

    private double calculateDistance(
            Double lat1,
            Double lon1,
            Double lat2,
            Double lon2) {

        final int R = 6371;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                        + Math.cos(
                                Math.toRadians(lat1)
                        )
                        * Math.cos(
                                Math.toRadians(lat2)
                        )
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return R * c;
    }


    // =========================================================
    // GET ASSISTANT BY EMAIL
    // =========================================================

    public Assistant getAssistantByEmail(String email) {

        return assistantRepository.findByEmail(email);
    }


    // =========================================================
    // ADMIN - ASSIGN ASSISTANT
    // =========================================================

    public Booking assignAssistant(
            Long bookingId,
            Long assistantId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking Not Found With ID : "
                                                + bookingId
                                )
                        );

        Assistant assistant =
                assistantRepository.findById(assistantId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assistant Not Found With ID : "
                                                + assistantId
                                )
                        );

        booking.setAssistantId(
                assistant.getId()
        );

        booking.setStatus("ASSIGNED");

        Booking updatedBooking =
                bookingRepository.save(booking);

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Assistant assigned for Booking #"
                        + updatedBooking.getId()
        );

        return updatedBooking;
    }


    // =========================================================
    // ASSISTANT - ACCEPT REQUEST
    // =========================================================

    public Booking acceptBooking(
            Long bookingId,
            Long assistantId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking Not Found With ID : "
                                                + bookingId
                                )
                        );


        // =====================================================
        // VERIFY SELECTED ASSISTANT
        // =====================================================

        if (booking.getAssistantId() == null ||
                !booking.getAssistantId()
                        .equals(assistantId)) {

            throw new AccessDeniedException(
                    "This booking is not assigned to you"
            );
        }


        // =====================================================
        // ONLY REQUESTED BOOKINGS CAN BE ACCEPTED
        // =====================================================

        if (!"REQUESTED".equalsIgnoreCase(
                booking.getStatus())) {

            throw new RuntimeException(
                    "Booking is no longer available"
            );
        }


        // =====================================================
        // CHANGE TO ASSIGNED
        // =====================================================

        booking.setStatus("ASSIGNED");

        Booking updatedBooking =
                bookingRepository.save(booking);


        // =====================================================
        // NOTIFY USER
        // =====================================================

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Assistant accepted your Booking #"
                        + updatedBooking.getId()
        );

        return updatedBooking;
    }


    // =========================================================
    // VERIFY OTP
    // =========================================================

    public String verifyOtp(
            Long bookingId,
            String otp) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking Not Found With ID : "
                                                + bookingId
                                )
                        );

        if (booking.getOtp().equals(otp)) {

            booking.setStatus("OTP_VERIFIED");

            Booking updatedBooking =
                    bookingRepository.save(booking);

            notificationService.createNotification(
                    updatedBooking.getUserId(),
                    "OTP verified successfully for Booking #"
                            + updatedBooking.getId()
            );

            return "OTP VERIFIED SUCCESSFULLY";
        }

        return "INVALID OTP";
    }


    // =========================================================
    // UPDATE BOOKING STATUS
    // =========================================================

    public Booking updateStatus(
            Long bookingId,
            String status) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking Not Found With ID : "
                                                + bookingId
                                )
                        );

        booking.setStatus(status);

        Booking updatedBooking =
                bookingRepository.save(booking);

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Booking #"
                        + updatedBooking.getId()
                        + " status updated to "
                        + status
        );

        return updatedBooking;
    }


    // =========================================================
    // REQUEST RETURN
    // =========================================================

    public Booking requestReturn(Long bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking Not Found With ID : "
                                                + bookingId
                                )
                        );

        booking.setStatus("RETURN_REQUESTED");

        Booking updatedBooking =
                bookingRepository.save(booking);

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Return requested for Booking #"
                        + updatedBooking.getId()
        );

        return updatedBooking;
    }


    // =========================================================
    // GET ALL AVAILABLE REQUESTS
    // =========================================================

    public List<Booking> getAvailableRequests() {

        return bookingRepository.findByStatus(
                "REQUESTED"
        );
    }


    // =========================================================
    // GET AVAILABLE REQUESTS FOR SPECIFIC ASSISTANT
    // =========================================================

    public List<Booking> getAvailableRequestsForAssistant(
            Long assistantId) {

        List<Booking> requestedBookings =
                bookingRepository.findByStatus(
                        "REQUESTED"
                );

        List<Booking> result =
                new ArrayList<>();

        for (Booking booking : requestedBookings) {

            if (booking.getAssistantId() != null &&
                    booking.getAssistantId()
                            .equals(assistantId)) {

                result.add(booking);
            }
        }

        return result;
    }


    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    // =========================================================
    // GET BOOKINGS BY USER
    // =========================================================

    public List<Booking> getBookingsByUser(
            Long userId) {

        return bookingRepository.findByUserId(
                userId
        );
    }


    // =========================================================
    // GET BOOKINGS BY ASSISTANT
    // =========================================================

    public List<Booking> getBookingsByAssistant(
            Long assistantId) {

        return bookingRepository.findByAssistantId(
                assistantId
        );
    }


    // =========================================================
    // AVAILABLE REQUEST DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getAvailableRequestDetails() {

        return convertToDTOList(
                bookingRepository.findByStatus(
                        "REQUESTED"
                )
        );
    }


    // =========================================================
    // AVAILABLE REQUEST DETAILS FOR ASSISTANT
    // =========================================================

    public List<BookingDetailsDTO>
    getAvailableRequestDetailsForAssistant(
            Long assistantId) {

        return convertToDTOList(
                getAvailableRequestsForAssistant(
                        assistantId
                )
        );
    }


    // =========================================================
    // ASSISTANT BOOKING DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getBookingDetailsByAssistant(
            Long assistantId) {

        return convertToDTOList(
                bookingRepository.findByAssistantId(
                        assistantId
                )
        );
    }


    // =========================================================
    // USER BOOKING DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getBookingDetailsByUser(
            Long userId) {

        return convertToDTOList(
                bookingRepository.findByUserId(
                        userId
                )
        );
    }


    // =========================================================
    // ALL BOOKING DETAILS
    // =========================================================

    public List<BookingDetailsDTO>
    getAllBookingDetails() {

        return convertToDTOList(
                bookingRepository.findAll()
        );
    }


    // =========================================================
    // CONVERT BOOKING LIST TO DTO
    // =========================================================

    private List<BookingDetailsDTO>
    convertToDTOList(
            List<Booking> bookings) {

        List<BookingDetailsDTO> detailsList =
                new ArrayList<>();

        for (Booking booking : bookings) {

            detailsList.add(
                    convertToDTO(booking)
            );
        }

        return detailsList;
    }


    // =========================================================
    // CONVERT BOOKING TO DTO
    // =========================================================

    private BookingDetailsDTO
    convertToDTO(Booking booking) {

        BookingDetailsDTO dto =
                new BookingDetailsDTO();


        dto.setId(
                booking.getId()
        );

        dto.setUserId(
                booking.getUserId()
        );

        dto.setVehicleId(
                booking.getVehicleId()
        );

        dto.setAssistantId(
                booking.getAssistantId()
        );

        dto.setPickupLocation(
                booking.getPickupLocation()
        );

        dto.setParkingLocation(
                booking.getParkingLocation()
        );

        dto.setStatus(
                booking.getStatus()
        );

        dto.setOtp(
                booking.getOtp()
        );

        dto.setPickupLat(
                booking.getPickupLat()
        );

        dto.setPickupLng(
                booking.getPickupLng()
        );

        dto.setParkingLat(
                booking.getParkingLat()
        );

        dto.setParkingLng(
                booking.getParkingLng()
        );


        // =====================================================
        // USER DETAILS
        // =====================================================

        if (booking.getUserId() != null) {

            userRepository
                    .findById(
                            booking.getUserId()
                    )
                    .ifPresent(user -> {

                        dto.setUserName(
                                user.getName()
                        );
                    });
        }


        // =====================================================
        // VEHICLE DETAILS
        // =====================================================

        if (booking.getVehicleId() != null) {

            vehicleRepository
                    .findById(
                            booking.getVehicleId()
                    )
                    .ifPresent(vehicle -> {

                        dto.setVehicleNumber(
                                vehicle.getVehicleNumber()
                        );

                        dto.setVehicleType(
                                vehicle.getVehicleType()
                        );
                    });
        }


        // =====================================================
        // ASSISTANT DETAILS
        // =====================================================

        if (booking.getAssistantId() != null) {

            assistantRepository
                    .findById(
                            booking.getAssistantId()
                    )
                    .ifPresent(assistant -> {

                        dto.setAssistantName(
                                assistant.getName()
                        );
                    });
        }


        return dto;
    }
}