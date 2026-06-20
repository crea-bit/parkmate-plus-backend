package com.parkmate.parkmateplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
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

    public Booking createBooking(Booking booking) {

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
                throw new RuntimeException("Parking location must be within 1 km");
            }
        }

        booking.setStatus("REQUESTED");
        booking.setAssistantId(null);

        String otp = String.valueOf(1000 + new Random().nextInt(9000));
        booking.setOtp(otp);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                savedBooking.getUserId(),
                "Your parking request has been created. Booking ID: " + savedBooking.getId()
        );

        return savedBooking;
    }

    private double calculateDistance(Double lat1,
                                     Double lon1,
                                     Double lat2,
                                     Double lon2) {

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public Booking assignAssistant(Long bookingId, Long assistantId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking Not Found With ID : " + bookingId
                ));

        booking.setAssistantId(assistantId);
        booking.setStatus("ASSIGNED");

        Booking updatedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Assistant assigned for Booking #" + updatedBooking.getId()
        );

        return updatedBooking;
    }

    public String verifyOtp(Long bookingId, String otp) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking Not Found With ID : " + bookingId
                ));

        if (booking.getOtp().equals(otp)) {
            booking.setStatus("OTP_VERIFIED");

            Booking updatedBooking = bookingRepository.save(booking);

            notificationService.createNotification(
                    updatedBooking.getUserId(),
                    "OTP verified successfully for Booking #" + updatedBooking.getId()
            );

            return "OTP VERIFIED SUCCESSFULLY";
        }

        return "INVALID OTP";
    }

    public Booking updateStatus(Long bookingId, String status) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking Not Found With ID : " + bookingId
                ));

        booking.setStatus(status);

        Booking updatedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Booking #" + updatedBooking.getId() + " status updated to " + status
        );

        return updatedBooking;
    }

    public Booking requestReturn(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking Not Found With ID : " + bookingId
                ));

        booking.setStatus("RETURN_REQUESTED");

        Booking updatedBooking = bookingRepository.save(booking);

        notificationService.createNotification(
                updatedBooking.getUserId(),
                "Return requested for Booking #" + updatedBooking.getId()
        );

        return updatedBooking;
    }

    public List<Booking> getAvailableRequests() {
        return bookingRepository.findByStatus("REQUESTED");
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByAssistant(Long assistantId) {
        return bookingRepository.findByAssistantId(assistantId);
    }

    public List<BookingDetailsDTO> getAvailableRequestDetails() {
        return convertToDTOList(bookingRepository.findByStatus("REQUESTED"));
    }

    public List<BookingDetailsDTO> getBookingDetailsByAssistant(Long assistantId) {
        return convertToDTOList(bookingRepository.findByAssistantId(assistantId));
    }

    public List<BookingDetailsDTO> getBookingDetailsByUser(Long userId) {
        return convertToDTOList(bookingRepository.findByUserId(userId));
    }

    public List<BookingDetailsDTO> getAllBookingDetails() {
        return convertToDTOList(bookingRepository.findAll());
    }

    private List<BookingDetailsDTO> convertToDTOList(List<Booking> bookings) {

        List<BookingDetailsDTO> detailsList = new ArrayList<>();

        for (Booking booking : bookings) {
            detailsList.add(convertToDTO(booking));
        }

        return detailsList;
    }

    private BookingDetailsDTO convertToDTO(Booking booking) {

        BookingDetailsDTO dto = new BookingDetailsDTO();

        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setVehicleId(booking.getVehicleId());
        dto.setAssistantId(booking.getAssistantId());

        dto.setPickupLocation(booking.getPickupLocation());
        dto.setParkingLocation(booking.getParkingLocation());
        dto.setStatus(booking.getStatus());
        dto.setOtp(booking.getOtp());

        dto.setPickupLat(booking.getPickupLat());
        dto.setPickupLng(booking.getPickupLng());
        dto.setParkingLat(booking.getParkingLat());
        dto.setParkingLng(booking.getParkingLng());

        if (booking.getUserId() != null) {
            userRepository.findById(booking.getUserId()).ifPresent(user -> {
                dto.setUserName(user.getName());
            });
        }

        if (booking.getVehicleId() != null) {
            vehicleRepository.findById(booking.getVehicleId()).ifPresent(vehicle -> {
                dto.setVehicleNumber(vehicle.getVehicleNumber());
                dto.setVehicleType(vehicle.getVehicleType());
            });
        }

        if (booking.getAssistantId() != null) {
            assistantRepository.findById(booking.getAssistantId()).ifPresent(assistant -> {
                dto.setAssistantName(assistant.getName());
            });
        }

        return dto;
    }
}