package com.parkmate.parkmateplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import com.parkmate.parkmateplus.service.AssistantService;

@SpringBootApplication
public class ParkmateplusApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkmateplusApplication.class, args);
    }
}