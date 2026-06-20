package com.parkmate.parkmateplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parkmate.parkmateplus.entity.Assistant;
import com.parkmate.parkmateplus.service.AssistantService;

@RestController
@RequestMapping("/assistants")
@CrossOrigin(origins = "*")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    @PostMapping("/register")
    public Assistant registerAssistant(@RequestBody Assistant assistant) {
        return assistantService.saveAssistant(assistant);
    }

    @PostMapping("/login")
    public Assistant loginAssistant(@RequestBody Assistant assistant) {
        return assistantService.login(assistant.getEmail());
    }
}