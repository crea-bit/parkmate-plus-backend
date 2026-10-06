package com.parkmate.parkmateplus.controller;

import com.parkmate.parkmateplus.entity.User;
import com.parkmate.parkmateplus.service.PaymentService;
import com.parkmate.parkmateplus.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.parkmate.parkmateplus.dto.PaymentVerifyRequest;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-order/{bookingId}")
    public ResponseEntity<?> createOrder(
            @PathVariable Long bookingId,
            Authentication authentication) {

        try {

            // Get logged-in user's email from JWT
            String email = authentication.getName();

            // Find user
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(404)
                        .body(Map.of(
                                "message",
                                "User not found"
                        ));
            }

            // Create Razorpay order
            Map<String, Object> response =
                    paymentService.createOrder(
                            bookingId,
                            user.getId()
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
 // =========================================================
 // VERIFY RAZORPAY PAYMENT
 // =========================================================

 @PreAuthorize("hasRole('USER')")
 @PostMapping("/verify")
 public ResponseEntity<?> verifyPayment(
         @RequestBody PaymentVerifyRequest request,
         Authentication authentication) {

     try {

         // Get logged-in user's email
         String email = authentication.getName();

         // Find user
         User user = userRepository.findByEmail(email);

         if (user == null) {
             return ResponseEntity.status(404)
                     .body(Map.of(
                             "message",
                             "User not found"
                     ));
         }

         // Verify payment
         Map<String, Object> response =
                 paymentService.verifyPayment(
                         request,
                         user.getId()
                 );

         return ResponseEntity.ok(response);

     } catch (RuntimeException e) {

         return ResponseEntity.badRequest()
                 .body(Map.of(
                         "message",
                         e.getMessage()
                 ));
     }
 }
//=========================================================
//GET PAYMENT STATUS
//=========================================================

@PreAuthorize("hasRole('USER')")
@GetMapping("/status/{bookingId}")
public ResponseEntity<?> getPaymentStatus(
      @PathVariable Long bookingId,
      Authentication authentication) {

  try {

      String email = authentication.getName();

      User user = userRepository.findByEmail(email);

      if (user == null) {
          return ResponseEntity.status(404)
                  .body(Map.of(
                          "message",
                          "User not found"
                  ));
      }

      Map<String, Object> response =
              paymentService.getPaymentStatus(
                      bookingId,
                      user.getId()
              );

      return ResponseEntity.ok(response);

  } catch (RuntimeException e) {

      return ResponseEntity.badRequest()
              .body(Map.of(
                      "message",
                      e.getMessage()
              ));
  }
}
}