package com.parkmate.parkmateplus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.repository.AssistantRepository;

@Service
public class AssistantService {

    @Autowired
    private AssistantRepository assistantRepository;

    public Assistant saveAssistant(Assistant assistant) {
        return assistantRepository.save(assistant);
    }

    public Assistant login(String email) {
        return assistantRepository.findByEmail(email);
    }
}