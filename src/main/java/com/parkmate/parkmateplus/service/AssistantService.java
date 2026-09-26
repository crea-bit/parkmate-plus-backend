package com.parkmate.parkmateplus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.repository.AssistantRepository;

@Service
public class AssistantService {

    @Autowired
    private AssistantRepository assistantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // REGISTER ASSISTANT
    // =========================
    public Assistant saveAssistant(Assistant assistant) {

        if (assistant.getPassword() != null
                && !assistant.getPassword().isBlank()
                && !assistant.getPassword().startsWith("$2a$")
                && !assistant.getPassword().startsWith("$2b$")
                && !assistant.getPassword().startsWith("$2y$")) {

            assistant.setPassword(
                    passwordEncoder.encode(assistant.getPassword())
            );
        }

        return assistantRepository.save(assistant);
    }

    // =========================
    // LOGIN ASSISTANT
    // =========================
    public Assistant login(String email, String password) {

        List<Assistant> assistants =
                assistantRepository.findAllByEmail(email);

        for (Assistant assistant : assistants) {

            if (assistant.getPassword() != null
                    && passwordEncoder.matches(
                            password,
                            assistant.getPassword())) {

                return assistant;
            }
        }

        return null;
    }

    // =========================
    // RESET TEST ASSISTANT PASSWORD
    // =========================
    public void resetTestAssistantPassword(
            Long assistantId,
            String newPassword) {

        Assistant assistant = assistantRepository.findById(assistantId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assistant Not Found With ID : "
                                        + assistantId));

        assistant.setPassword(
                passwordEncoder.encode(newPassword)
        );

        assistantRepository.save(assistant);
    }
}