package com.parkmate.parkmateplus.service;

import com.parkmate.parkmateplus.entity.Booking;
import com.parkmate.parkmateplus.entity.Payment;
import com.parkmate.parkmateplus.repository.BookingRepository;
import com.parkmate.parkmateplus.repository.PaymentRepository;
import com.parkmate.parkmateplus.config.RazorpayConfig.RazorpayCredentials;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import java.util.HashMap;
import java.util.Map;

import com.parkmate.parkmateplus.dto.PaymentVerifyRequest;
import com.razorpay.Utils;

@Service
public class PaymentService {

    // Fixed demo service fee: ₹100
    // Razorpay uses paise, so ₹100 = 10000 paise
    private static final long PAYMENT_AMOUNT = 10000L;

    private static final String CURRENCY = "INR";

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RazorpayCredentials razorpayCredentials;


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    public Map<String, Object> createOrder(Long bookingId, Long userId) {

        // 1. Find booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));


        // 2. Check booking ownership
        if (booking.getUserId() == null ||
                !booking.getUserId().equals(userId)) {

            throw new RuntimeException(
                    "You are not authorized to pay for this booking"
            );
        }


        // 3. Payment is allowed ONLY after vehicle is returned
        if (!"COMPLETED".equalsIgnoreCase(booking.getStatus())) {

            throw new RuntimeException(
                    "Payment is available only after the booking is COMPLETED"
            );
        }


        // 4. Prevent duplicate successful payment
        if (paymentRepository.existsByBookingIdAndStatus(
                bookingId, "PAID")) {

            throw new RuntimeException(
                    "Payment has already been completed for this booking"
            );
        }


        // =====================================================
        // 4.1 CHECK FOR EXISTING PAYMENT
        // =====================================================

        Payment existingPayment = paymentRepository
                .findByBookingId(bookingId)
                .orElse(null);

        /*
         * If a payment record already exists for this booking
         * and it is not PAID, reuse the existing Razorpay order.
         *
         * This prevents:
         *
         * duplicate key value
         * booking_id=(6) already exists
         */

        if (existingPayment != null) {

            // If already paid
            if ("PAID".equalsIgnoreCase(existingPayment.getStatus())) {

                throw new RuntimeException(
                        "Payment has already been completed for this booking"
                );
            }


            // Reuse existing Razorpay order
            if (existingPayment.getRazorpayOrderId() != null) {

                Map<String, Object> response =
                        new HashMap<>();

                response.put(
                        "orderId",
                        existingPayment.getRazorpayOrderId()
                );

                response.put(
                        "amount",
                        existingPayment.getAmount()
                );

                response.put(
                        "currency",
                        existingPayment.getCurrency()
                );

                response.put(
                        "keyId",
                        razorpayCredentials.getKeyId()
                );

                response.put(
                        "bookingId",
                        bookingId
                );

                return response;
            }
        }


        // =====================================================
        // CREATE NEW RAZORPAY ORDER
        // =====================================================

        try {

            // 5. Connect to Razorpay
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayCredentials.getKeyId(),
                            razorpayCredentials.getKeySecret()
                    );


            // 6. Create Razorpay order request
            JSONObject orderRequest =
                    new JSONObject();

            orderRequest.put(
                    "amount",
                    PAYMENT_AMOUNT
            );

            orderRequest.put(
                    "currency",
                    CURRENCY
            );


            // Receipt must be unique
            orderRequest.put(
                    "receipt",
                    "booking_" + bookingId
            );


            // 7. Create Razorpay order
            Order razorpayOrder =
                    razorpayClient.orders.create(orderRequest);


            // 8. Get Razorpay order ID
            String razorpayOrderId =
                    razorpayOrder.get("id");


            // 9. Save payment record
            Payment payment =
                    new Payment();

            payment.setBookingId(
                    bookingId
            );

            payment.setRazorpayOrderId(
                    razorpayOrderId
            );

            payment.setAmount(
                    PAYMENT_AMOUNT
            );

            payment.setCurrency(
                    CURRENCY
            );

            payment.setStatus(
                    "CREATED"
            );


            paymentRepository.save(payment);


            // 10. Send required information to frontend
            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "orderId",
                    razorpayOrderId
            );

            response.put(
                    "amount",
                    PAYMENT_AMOUNT
            );

            response.put(
                    "currency",
                    CURRENCY
            );

            response.put(
                    "keyId",
                    razorpayCredentials.getKeyId()
            );

            response.put(
                    "bookingId",
                    bookingId
            );


            return response;


        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create Razorpay order: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    // =========================================================

    public Map<String, Object> verifyPayment(
            PaymentVerifyRequest request,
            Long userId) {


        // 1. Basic validation
        if (request.getBookingId() == null ||
                request.getRazorpayOrderId() == null ||
                request.getRazorpayPaymentId() == null ||
                request.getRazorpaySignature() == null) {

            throw new RuntimeException(
                    "Payment verification details are incomplete"
            );
        }


        // 2. Find booking
        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        ));


        // 3. Check booking ownership
        if (booking.getUserId() == null ||
                !booking.getUserId().equals(userId)) {

            throw new RuntimeException(
                    "You are not authorized to verify this payment"
            );
        }


        // 4. Payment is valid only for COMPLETED booking
        if (!"COMPLETED".equalsIgnoreCase(
                booking.getStatus())) {

            throw new RuntimeException(
                    "Payment is allowed only after booking completion"
            );
        }


        // 5. Find our saved payment record
        Payment payment = paymentRepository
                .findByBookingId(request.getBookingId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment order not found"
                        ));


        // 6. Prevent duplicate successful payment
        if ("PAID".equalsIgnoreCase(
                payment.getStatus())) {

            throw new RuntimeException(
                    "Payment has already been completed"
            );
        }


        // 7. Make sure Razorpay order matches our order
        if (payment.getRazorpayOrderId() == null ||
                !payment.getRazorpayOrderId()
                        .equals(request.getRazorpayOrderId())) {

            throw new RuntimeException(
                    "Razorpay order ID does not match"
            );
        }


        try {

            // 8. Prepare signature verification data
            JSONObject attributes =
                    new JSONObject();

            attributes.put(
                    "razorpay_order_id",
                    request.getRazorpayOrderId()
            );

            attributes.put(
                    "razorpay_payment_id",
                    request.getRazorpayPaymentId()
            );

            attributes.put(
                    "razorpay_signature",
                    request.getRazorpaySignature()
            );


            // 9. Verify Razorpay signature
            Utils.verifyPaymentSignature(
                    attributes,
                    razorpayCredentials.getKeySecret()
            );


            // 10. Signature is valid

            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );

            payment.setPaymentMethod(
                    request.getPaymentMethod()
            );

            payment.setStatus(
                    "PAID"
            );


            paymentRepository.save(
                    payment
            );


            // 11. Response
            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Payment successful"
            );

            response.put(
                    "bookingId",
                    request.getBookingId()
            );

            response.put(
                    "paymentId",
                    request.getRazorpayPaymentId()
            );

            response.put(
                    "orderId",
                    request.getRazorpayOrderId()
            );

            response.put(
                    "status",
                    "PAID"
            );


            return response;


        } catch (Exception e) {

            throw new RuntimeException(
                    "Payment verification failed"
            );
        }
    }


    // =========================================================
    // GET PAYMENT STATUS
    // =========================================================

    public Map<String, Object> getPaymentStatus(
            Long bookingId,
            Long userId) {


        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        ));


        // Check ownership
        if (booking.getUserId() == null ||
                !booking.getUserId().equals(userId)) {

            throw new RuntimeException(
                    "You are not authorized to view this payment"
            );
        }


        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "bookingId",
                bookingId
        );


        Payment payment = paymentRepository
                .findByBookingId(bookingId)
                .orElse(null);


        if (payment == null) {

            response.put(
                    "status",
                    "NOT_PAID"
            );

            response.put(
                    "amount",
                    10000L
            );

            response.put(
                    "currency",
                    "INR"
            );

        } else {

            response.put(
                    "status",
                    payment.getStatus()
            );

            response.put(
                    "amount",
                    payment.getAmount()
            );

            response.put(
                    "currency",
                    payment.getCurrency()
            );

            response.put(
                    "paymentId",
                    payment.getRazorpayPaymentId()
            );
        }


        return response;
    }
}