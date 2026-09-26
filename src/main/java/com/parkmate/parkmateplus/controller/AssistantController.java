package com.parkmate.parkmateplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.service.AssistantService;
import com.parkmate.parkmateplus.service.JwtService;

@RestController
@RequestMapping("/assistants")
@CrossOrigin(origins = "*")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private JwtService jwtService;


    // =========================================================
    // ASSISTANT REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Assistant assistant) {

        try {

            Assistant savedAssistant =
                    assistantService.saveAssistant(assistant);

            return ResponseEntity.ok(savedAssistant);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // ASSISTANT LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Assistant assistant) {

        try {

            Assistant loggedInAssistant =
                    assistantService.login(
                            assistant.getEmail(),
                            assistant.getPassword()
                    );


            // -------------------------------------------------
            // INVALID LOGIN
            // -------------------------------------------------

            if (loggedInAssistant == null) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }


            // -------------------------------------------------
            // GENERATE JWT
            // -------------------------------------------------

            String token =
                    jwtService.generateToken(
                            loggedInAssistant
                    );


            // -------------------------------------------------
            // RETURN TOKEN + ASSISTANT
            // -------------------------------------------------

            return ResponseEntity.ok(
                    new AssistantLoginResponse(
                            token,
                            loggedInAssistant
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // LOGIN RESPONSE
    // =========================================================

    public static class AssistantLoginResponse {

        private String token;

        private Assistant assistant;


        public AssistantLoginResponse() {
        }


        public AssistantLoginResponse(
                String token,
                Assistant assistant) {

            this.token = token;
            this.assistant = assistant;
        }


        public String getToken() {
            return token;
        }


        public void setToken(String token) {
            this.token = token;
        }


        public Assistant getAssistant() {
            return assistant;
        }


        public void setAssistant(
                Assistant assistant) {

            this.assistant = assistant;
        }
    }
}